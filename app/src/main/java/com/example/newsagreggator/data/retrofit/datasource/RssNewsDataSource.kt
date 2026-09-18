package com.example.newsagreggator.data.retrofit.datasource

import android.util.Log
import com.example.newsagreggator.data.retrofit.newssource.NewsFeed
import com.example.newsagreggator.data.retrofit.newssource.NewsSource
import com.example.newsagreggator.data.retrofit.parser.RssFeedParser
import com.example.newsagreggator.data.retrofit.newssource.RssNewsService
import com.example.newsagreggator.model.Article
import com.example.newsagreggator.location.ArticleCityClassifier
import com.example.newsagreggator.util.TextNormalizer
import com.example.newsagreggator.hilt.NewsSourceCatalog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RssNewsDataSource @Inject constructor(
    private val newsService: RssNewsService,
    @NewsSourceCatalog sources: List<NewsSource>,
    private val parser: RssFeedParser,
    private val articleCityClassifier: ArticleCityClassifier,
    private val textNormalizer: TextNormalizer,
) : RemoteNewsDataSource {
    private val sourceFeeds = sources.flatMap { source ->
        source.feeds.map { feed -> SourceFeed(source, feed) }
    }


    //Input is in format: Success([RTS article1, RTS article 2]), Failure(N1 network error)
    private fun convertToArticles(results: List<Result<List<Article>>>): List<Article> {
        return results
            //returns List<Article> or null
            .mapNotNull { result -> result.getOrNull() }
            //combines list data: List<Article>
            .flatten()
            //remove duplicate articles
            .distinctBy(Article::articleUrl)
            .map { article ->
                val normalizedArticle = article.copy(
                    normalizedText = textNormalizer.normalize(
                        "${article.title} ${article.summary} ${article.source}"
                    )
                )
                normalizedArticle.copy(
                    relatedCityIds =
                        articleCityClassifier.classify(normalizedArticle)
                )
            }
            .sortedByDescending(Article::publishedAtEpochMillis)
    }

    override suspend fun fetchArticles(): List<Article> =
        withContext(Dispatchers.IO) {
            coroutineScope {

                //fetching news from all feeds
                val results = sourceFeeds.map { sourceFeed ->
                    async {
                        fetchFeed(sourceFeed)
                    }
                }.awaitAll()

                val articles = convertToArticles(results)

                if (articles.isEmpty()) {
                    val error = IllegalStateException(
                        "None of the configured RSS feeds returned usable articles"
                    )
                    results.mapNotNull { result -> result.exceptionOrNull() }
                        .forEach(error::addSuppressed)
                    throw error
                }

                articles
            }
        }

    //getting xml formatted news list feed from news soruce
    private suspend fun fetchFeed(
        sourceFeed: SourceFeed,
    ): Result<List<Article>> {
        val source = sourceFeed.source
        val feed = sourceFeed.feed

        return try {
            Result.success(
                parser.parse(
                    feedXml = newsService.getFeed(feed.url),
                    source = source,
                    forcedCategory = feed.category,
                )
            )
        }
        catch (cancellation: CancellationException) {
            throw cancellation
        }
        catch (error: Exception) {
            Log.w(
                TAG,
                "Could not load ${source.displayName} feed: ${feed.url}",
                error,
            )
            Result.failure(error)
        }
    }

    private data class SourceFeed(
        val source: NewsSource,
        val feed: NewsFeed,
    )

    private companion object {
        const val TAG = "RssNewsDataSource"
    }
}