package com.example.newsagreggator.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.newsagreggator.data.database.entity.ArticleEntity
import com.example.newsagreggator.data.database.entity.NewsSyncMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArticleDao {
    @Query("SELECT * FROM articles ORDER BY publishedAtEpochMillis DESC")
    abstract fun observeArticles(): Flow<List<ArticleEntity>>

    @Query(
        """
        SELECT lastSuccessfulRefreshEpochMillis
        FROM news_sync_metadata
        WHERE id = 1
        """
    )
    abstract fun observeLastSuccessfulRefresh(): Flow<Long?>

    @Query(
        """
        SELECT lastSuccessfulRefreshEpochMillis
        FROM news_sync_metadata
        WHERE id = 1
        """
    )
    abstract suspend fun getLastSuccessfulRefresh(): Long?

    @Upsert
    protected abstract suspend fun upsertArticles(
        articles: List<ArticleEntity>,
    )

    @Upsert
    protected abstract suspend fun upsertSyncMetadata(
        metadata: NewsSyncMetadataEntity,
    )

    @Query(
        """
        DELETE FROM articles
        WHERE id NOT IN (:currentArticleIds)
          AND id NOT IN (
              SELECT articleId
              FROM article_state
              WHERE isSaved = 1 OR isRead = 1
          )
        """
    )
    protected abstract suspend fun deleteStaleArticles(
        currentArticleIds: List<String>,
    )

    @Transaction
    open suspend fun replaceRemoteArticles(
        articles: List<ArticleEntity>,
        refreshedAtEpochMillis: Long,
    ) {
        upsertArticles(articles)
        deleteStaleArticles(articles.map(ArticleEntity::id))
        upsertSyncMetadata(
            NewsSyncMetadataEntity(
                lastSuccessfulRefreshEpochMillis = refreshedAtEpochMillis
            )
        )
    }
}
