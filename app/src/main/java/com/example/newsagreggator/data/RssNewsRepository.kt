package com.example.newsagreggator.data

import com.example.newsagreggator.data.remote.RemoteNewsDataSource
import com.example.newsagreggator.domain.model.Article
import kotlinx.coroutines.CancellationException

class RssNewsRepository(
    private val remoteNewsDataSource: RemoteNewsDataSource,
    initialArticles: List<Article> = createSampleNewsArticles(),
) : NewsRepository {
    private var articles = initialArticles

    override fun getArticles(): List<Article> = articles

    override suspend fun refreshArticles(): Result<List<Article>> {
        return try {
            remoteNewsDataSource.fetchArticles()
                .also { fetchedArticles ->
                    check(fetchedArticles.isNotEmpty()) {
                        "The RSS feed did not contain usable articles"
                    }
                    articles = fetchedArticles
                }
                .let(Result.Companion::success)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            Result.failure(error)
        }
    }
}
