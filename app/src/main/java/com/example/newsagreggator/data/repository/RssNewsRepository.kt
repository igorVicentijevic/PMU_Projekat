package com.example.newsagreggator.data.repository

import com.example.newsagreggator.data.local.dao.ArticleDao
import com.example.newsagreggator.data.local.entities.toDomain
import com.example.newsagreggator.data.local.entities.toEntity
import com.example.newsagreggator.data.remote.RemoteNewsDataSource
import com.example.newsagreggator.data.sample.createSampleNewsArticles
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
