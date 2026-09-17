package com.example.newsagreggator.data.local.repository

import com.example.newsagreggator.data.local.dao.ArticleStateDao
import com.example.newsagreggator.data.local.entities.ArticleStateEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

data class ArticleState(
    val savedArticleIds: Set<String> = emptySet(),
    val readArticleIds: Set<String> = emptySet(),
)

interface ArticleStateRepository {
    val articleState: Flow<ArticleState>

    suspend fun setArticleSaved(articleId: String, saved: Boolean)

    suspend fun markArticleRead(articleId: String)

    suspend fun clearReadingHistory()
}

class RoomArticleStateRepository @Inject constructor(
    private val articleStateDao: ArticleStateDao,
) : ArticleStateRepository {
    override val articleState: Flow<ArticleState> =
        articleStateDao.observeArticleStates().map { states ->
            ArticleState(
                savedArticleIds = states
                    .filter(ArticleStateEntity::isSaved)
                    .mapTo(mutableSetOf(), ArticleStateEntity::articleId),
                readArticleIds = states
                    .filter(ArticleStateEntity::isRead)
                    .mapTo(mutableSetOf(), ArticleStateEntity::articleId),
            )
        }

    override suspend fun setArticleSaved(articleId: String, saved: Boolean) {
        articleStateDao.setArticleSaved(articleId, saved)
    }

    override suspend fun markArticleRead(articleId: String) {
        articleStateDao.markArticleRead(articleId)
    }

    override suspend fun clearReadingHistory() {
        articleStateDao.clearReadingHistory()
    }
}

class InMemoryArticleStateRepository : ArticleStateRepository {
    private val state = MutableStateFlow(ArticleState())

    override val articleState: Flow<ArticleState> = state

    override suspend fun setArticleSaved(articleId: String, saved: Boolean) {
        state.update { currentState ->
            currentState.copy(
                savedArticleIds = if (saved) {
                    currentState.savedArticleIds + articleId
                } else {
                    currentState.savedArticleIds - articleId
                }
            )
        }
    }

    override suspend fun markArticleRead(articleId: String) {
        state.update { currentState ->
            currentState.copy(
                readArticleIds = currentState.readArticleIds + articleId
            )
        }
    }

    override suspend fun clearReadingHistory() {
        state.update { currentState ->
            currentState.copy(readArticleIds = emptySet())
        }
    }
}
