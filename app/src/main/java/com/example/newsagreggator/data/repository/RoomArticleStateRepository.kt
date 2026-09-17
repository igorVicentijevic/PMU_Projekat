package com.example.newsagreggator.data.repository

import com.example.newsagreggator.business.repository.ArticleState
import com.example.newsagreggator.business.repository.ArticleStateRepository
import com.example.newsagreggator.data.database.dao.ArticleStateDao
import com.example.newsagreggator.data.database.entity.ArticleStateEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
