package com.example.newsagreggator.repository

import com.example.newsagreggator.database.dao.ArticleDao
import com.example.newsagreggator.database.entity.toDomain
import com.example.newsagreggator.database.entity.toEntity
import com.example.newsagreggator.remote.RemoteNewsDataSource
import com.example.newsagreggator.di.InitialArticles
import com.example.newsagreggator.domain.model.Article
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class RssNewsRepository @Inject constructor(
    private val remoteNewsDataSource: RemoteNewsDataSource,
    private val articleDao: ArticleDao,
    @InitialArticles initialArticles: List<Article>,
) : NewsRepository {
    private val refreshMutex = Mutex()

    override val news: Flow<NewsSnapshot> = combine(
        articleDao.observeArticles(),
        articleDao.observeLastSuccessfulRefresh(),
    ) { cachedArticles, lastSuccessfulRefresh ->

        val articles = cachedArticles
            .mapNotNull { it.toDomain() }
            .ifEmpty { initialArticles }

        NewsSnapshot(
            articles = articles,
            lastSuccessfulRefreshEpochMillis = lastSuccessfulRefresh,
        )

    }

    //synchronization is needed because refreshing should be done concurrently from background worker
    //and foreground process
    override suspend fun refreshArticles(): Result<Unit> = refreshMutex.withLock {
        refreshArticlesLocked()
    }

    override suspend fun refreshArticlesIfStale(
        staleAfterMillis: Long,
    ): Result<Unit> = refreshMutex.withLock {
        val lastSuccessfulRefresh = articleDao.getLastSuccessfulRefresh()
        val isFresh = lastSuccessfulRefresh != null &&
            System.currentTimeMillis() - lastSuccessfulRefresh < staleAfterMillis

        if (isFresh) {
            Result.success(Unit)
        } else {
            refreshArticlesLocked()
        }
    }

    private suspend fun refreshArticlesLocked(): Result<Unit> {
        return try {
            //getting articles from the internet
            val fetchedArticles = remoteNewsDataSource.fetchArticles()
            check(fetchedArticles.isNotEmpty()) {
                "The RSS feed did not contain usable articles"
            }

            //updating database
            articleDao.replaceRemoteArticles(
                articles = fetchedArticles.map(Article::toEntity),
                refreshedAtEpochMillis = System.currentTimeMillis(),
            )
            Result.success(Unit)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}
