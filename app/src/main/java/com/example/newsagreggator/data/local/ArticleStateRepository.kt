package com.example.newsagreggator.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

data class ArticleState(
    val savedArticleIds: Set<Int> = emptySet(),
    val readArticleIds: Set<Int> = emptySet(),
)

interface ArticleStateRepository {
    val articleState: Flow<ArticleState>

    suspend fun setArticleSaved(articleId: Int, saved: Boolean)

    suspend fun markArticleRead(articleId: Int)

    suspend fun clearReadingHistory()
}

class RoomArticleStateRepository(
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

    override suspend fun setArticleSaved(articleId: Int, saved: Boolean) {
        articleStateDao.setArticleSaved(articleId, saved)
    }

    override suspend fun markArticleRead(articleId: Int) {
        articleStateDao.markArticleRead(articleId)
    }

    override suspend fun clearReadingHistory() {
        articleStateDao.clearReadingHistory()
    }
}

class InMemoryArticleStateRepository : ArticleStateRepository {
    private val state = MutableStateFlow(ArticleState())

    override val articleState: Flow<ArticleState> = state

    override suspend fun setArticleSaved(articleId: Int, saved: Boolean) {
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

    override suspend fun markArticleRead(articleId: Int) {
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
