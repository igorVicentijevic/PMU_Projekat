package com.example.newsagreggator.data

import com.example.newsagreggator.data.local.ArticleDao
import com.example.newsagreggator.data.local.toDomain
import com.example.newsagreggator.data.local.toEntity
import com.example.newsagreggator.data.remote.RemoteNewsDataSource
import com.example.newsagreggator.domain.model.Article
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class RssNewsRepository(
    private val remoteNewsDataSource: RemoteNewsDataSource,
    private val articleDao: ArticleDao,
    initialArticles: List<Article> = createSampleNewsArticles(),
) : NewsRepository {
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

    override suspend fun refreshArticles(): Result<Unit> {
        return try {
            val fetchedArticles = remoteNewsDataSource.fetchArticles()
            check(fetchedArticles.isNotEmpty()) {
                "The RSS feed did not contain usable articles"
            }
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
