package com.example.newsagreggator.data.remote.parser

import android.text.Html
import android.util.Xml
import com.example.newsagreggator.data.remote.source.NewsSource
import com.example.newsagreggator.domain.model.Article
import com.example.newsagreggator.domain.model.NewsCategory
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Locale

class RssFeedParser {
    fun parse(
        feedXml: String,
        source: NewsSource,
        forcedCategory: NewsCategory? = null,
    ): List<Article> {
        StringReader(feedXml).use { reader ->
            val parser = Xml.newPullParser().apply {
                setInput(reader)
            }
            val articles = mutableListOf<Article>()
            var item: RssItemBuilder? = null

            while (parser.eventType != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType == XmlPullParser.START_TAG) {
                    val tagName = parser.name
                    when (tagName.substringAfter(':')) {
                        "item" -> item = RssItemBuilder()
                        "title" -> item?.title = parser.nextText().cleanHtml()
                        "link" -> item?.link = parser.nextText().trim()
                        "description" -> {
                            item?.descriptionHtml = parser.nextText()
                        }
                        "pubDate" -> item?.publishedAt = parser.nextText().trim()
                        "category" -> {
                            item?.categories?.add(parser.nextText().cleanHtml())
                        }
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
                        "content", "thumbnail", "enclosure" -> {
                            val imageUrl = parser.getAttributeValue(null, "url")
                            if (
                                item?.imageUrl == null &&
                                !imageUrl.isNullOrBlank() &&
                                tagName != "content:encoded"
                            ) {
                                item?.imageUrl = imageUrl
                            }
                        }
                        "encoded" -> {
                            if (item?.descriptionHtml.isNullOrBlank()) {
                                item?.descriptionHtml = parser.nextText()
                            }
                        }
                    }
                } else if (
                    parser.eventType == XmlPullParser.END_TAG &&
                    parser.name.substringAfter(':') == "item"
                ) {
                    item?.toArticle(
                        source = source,
                        forcedCategory = forcedCategory,
                    )?.let(articles::add)
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
        var descriptionHtml: String = "",
        var publishedAt: String = "",
        val categories: MutableList<String> = mutableListOf(),
        var guid: String = "",
        var imageUrl: String? = null,
    ) {
        fun toArticle(
            source: NewsSource,
            forcedCategory: NewsCategory?,
        ): Article? {
            val articleUrl = link.toWebUrlOrNull() ?: return null
            if (title.isBlank()) return null

            return Article(
                id = guid.ifBlank { articleUrl },
                title = title,
                summary = descriptionHtml.cleanHtml(),
                source = source.displayName,
                category = forcedCategory
                    ?: categories.joinToString(" ").toNewsCategory(),
                publishedAtEpochMillis = publishedAt.toEpochMillis(),
                imageUrl = (imageUrl ?: descriptionHtml.findFirstImageUrl())
                    ?.let(source.imageUrlNormalizer)
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
        val normalizedUrl = if (startsWith("//")) "https:$this" else this
        URI(normalizedUrl).takeIf { uri ->
            uri.host != null &&
                (uri.scheme == "https" || uri.scheme == "http")
        }?.toString()
    }.getOrNull()

private fun String.findFirstImageUrl(): String? =
    Regex(
        pattern = """<img[^>]+src\s*=\s*["']([^"']+)["']""",
        option = RegexOption.IGNORE_CASE,
    ).find(this)?.groupValues?.getOrNull(1)

private fun String.toNewsCategory(): NewsCategory {
    val normalized = lowercase(Locale.forLanguageTag("sr"))
    return when {
        "спорт" in normalized || "sport" in normalized -> NewsCategory.Sport
        "култур" in normalized || "kultur" in normalized -> NewsCategory.Culture
        "економ" in normalized ||
            "biznis" in normalized ||
            "privreda" in normalized ->
            NewsCategory.Business
        "здрав" in normalized || "health" in normalized -> NewsCategory.Health
        "технолог" in normalized ||
            "наук" in normalized ||
            "digital" in normalized -> NewsCategory.Technology
        "свет" in normalized ||
            "svet" in normalized ||
            "region" in normalized ||
            "evropa" in normalized ||
            "украјин" in normalized -> NewsCategory.World
        else -> NewsCategory.Serbia
    }
}
