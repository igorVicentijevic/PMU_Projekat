package com.example.newsagreggator.data.remote.rss

import android.util.Log
import com.example.newsagreggator.data.remote.RemoteNewsDataSource
import com.example.newsagreggator.data.remote.api.RssNewsService
import com.example.newsagreggator.data.remote.parser.RssFeedParser
import com.example.newsagreggator.data.remote.source.NewsFeed
import com.example.newsagreggator.data.remote.source.NewsSource
import com.example.newsagreggator.data.remote.source.NewsSources
import com.example.newsagreggator.domain.model.Article
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class RssNewsDataSource(
    private val newsService: RssNewsService,
    sources: List<NewsSource> = NewsSources.all,
    private val parser: RssFeedParser = RssFeedParser(),
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
