package com.example.newsagreggator.data.remote

import android.text.Html
import android.util.Xml
import com.example.newsagreggator.domain.model.Article
import com.example.newsagreggator.domain.model.NewsCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Locale

class RtsRssNewsDataSource(
    private val newsService: RtsNewsService,
) : RemoteNewsDataSource {
    override suspend fun fetchArticles(): List<Article> =
        withContext(Dispatchers.IO) {
            parseFeed(newsService.getNewsFeed())
        }

    private fun parseFeed(feedXml: String): List<Article> {
        StringReader(feedXml).use { reader ->
            val parser = Xml.newPullParser().apply {
                setInput(reader)
            }
            val articles = mutableListOf<Article>()
            var item: RssItemBuilder? = null

            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG) {
                    when (parser.name.substringAfter(':')) {
                        "item" -> item = RssItemBuilder()
                        "title" -> item?.title = parser.nextText().cleanHtml()
                        "link" -> item?.link = parser.nextText().trim()
                        "description" -> {
                            item?.description = parser.nextText().cleanHtml()
                        }
                        "pubDate" -> item?.publishedAt = parser.nextText().trim()
                        "category" -> item?.category = parser.nextText().cleanHtml()
                        "guid" -> item?.guid = parser.nextText().trim()
                        "img" -> {
                            val imageUrl = parser.getAttributeValue(null, "src")
                            if (
                                item?.imageUrl == null &&
                                !imageUrl.isNullOrBlank()
                            ) {
                                item?.imageUrl = imageUrl
                            }
                        }
                    }
                } else if (
                    parser.eventType == XmlPullParser.END_TAG &&
                    parser.name.substringAfter(':') == "item"
                ) {
                    item?.toArticle()?.let(articles::add)
                    item = null
                }
                parser.next()
            }

            return articles
                .distinctBy(Article::id)
                .sortedByDescending(Article::publishedAtEpochMillis)
        }
    }

    private data class RssItemBuilder(
        var title: String = "",
        var link: String = "",
        var description: String = "",
        var publishedAt: String = "",
        var category: String = "",
        var guid: String = "",
        var imageUrl: String? = null,
    ) {
        fun toArticle(): Article? {
            val articleUrl = link.toWebUrlOrNull() ?: return null
            if (title.isBlank()) return null

            return Article(
                id = guid.ifBlank { articleUrl },
                title = title,
                summary = description,
                source = "RTS",
                category = category.toNewsCategory(),
                publishedAtEpochMillis = publishedAt.toEpochMillis(),
                imageUrl = imageUrl
                    ?.normalizeRtsThumbnailUrl()
                    ?.toWebUrlOrNull(),
                articleUrl = articleUrl,
            )
        }
    }
}

private fun String.cleanHtml(): String =
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        .toString()
        .replace(Regex("\\s+"), " ")
        .trim()

private fun String.toEpochMillis(): Long =
    runCatching {
        SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss Z",
            Locale.ENGLISH,
        ).parse(this)?.time
    }.getOrNull() ?: System.currentTimeMillis()

private fun String.toWebUrlOrNull(): String? =
    runCatching {
        URI(this).takeIf { uri ->
            uri.host != null &&
                (uri.scheme == "https" || uri.scheme == "http")
        }?.toString()
    }.getOrNull()

private fun String.normalizeRtsThumbnailUrl(): String {
    val normalizedPath = replace(
        oldValue = "/upload/thumbnail//",
        newValue = "/upload//",
    )
    val lastPathSeparator = normalizedPath.lastIndexOf('/')
    if (lastPathSeparator <= 0) return normalizedPath

    val fileName = normalizedPath.substring(lastPathSeparator + 1)
    val parentPath = normalizedPath.substring(0, lastPathSeparator)
    return if (parentPath.endsWith("/$fileName")) {
        parentPath
    } else {
        normalizedPath
    }
}

private fun String.toNewsCategory(): NewsCategory {
    val normalized = lowercase(Locale.forLanguageTag("sr"))
    return when {
        "спорт" in normalized || "sport" in normalized -> NewsCategory.Sport
        "култур" in normalized || "kultur" in normalized -> NewsCategory.Culture
        "економ" in normalized || "biznis" in normalized ->
            NewsCategory.Business
        "здрав" in normalized -> NewsCategory.Health
        "технолог" in normalized ||
            "наук" in normalized ||
            "digital" in normalized -> NewsCategory.Technology
        "свет" in normalized ||
            "svet" in normalized ||
            "украјин" in normalized -> NewsCategory.World
        else -> NewsCategory.Serbia
    }
}
