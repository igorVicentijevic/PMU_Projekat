package com.example.newsagreggator.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArticleStateDao {
    @Query("SELECT * FROM article_state")
    abstract fun observeArticleStates(): Flow<List<ArticleStateEntity>>

    @Query("SELECT * FROM article_state WHERE articleId = :articleId")
    protected abstract suspend fun getArticleState(
        articleId: String,
    ): ArticleStateEntity?

    @Upsert
    protected abstract suspend fun upsertArticleState(
        articleState: ArticleStateEntity,
    )

    @Query("UPDATE article_state SET isRead = 0")
    abstract suspend fun clearReadingHistory()

    @Transaction
    open suspend fun setArticleSaved(articleId: String, saved: Boolean) {
        val articleState = getArticleState(articleId)
            ?: ArticleStateEntity(articleId = articleId)
        upsertArticleState(articleState.copy(isSaved = saved))
    }

    @Transaction
    open suspend fun markArticleRead(articleId: String) {
        val articleState = getArticleState(articleId)
            ?: ArticleStateEntity(articleId = articleId)
        upsertArticleState(articleState.copy(isRead = true))
    }
}
