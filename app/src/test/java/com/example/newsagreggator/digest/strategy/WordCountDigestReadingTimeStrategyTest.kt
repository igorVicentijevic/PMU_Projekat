package com.example.newsagreggator.digest.strategy

import com.example.newsagreggator.R
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import org.junit.Assert.assertEquals
import org.junit.Test

class WordCountDigestReadingTimeStrategyTest {
    private val strategy = WordCountDigestReadingTimeStrategy()

    @Test
    fun roundsPartialMinutesUp() {
        val article = article(
            title = words(75),
            summary = words(76),
        )

        assertEquals(2, strategy.estimateMinutes(listOf(article)))
    }

    @Test
    fun combinesAllDigestArticles() {
        val articles = listOf(
            article(title = words(50), summary = words(25)),
            article(title = words(25), summary = words(50)),
        )

        assertEquals(1, strategy.estimateMinutes(articles))
    }

    @Test
    fun returnsZeroForDigestWithoutReadableText() {
        val emptyArticle = article(title = " ", summary = "\n\t")

        assertEquals(0, strategy.estimateMinutes(listOf(emptyArticle)))
        assertEquals(0, strategy.estimateMinutes(emptyList()))
    }

    private fun words(count: Int): String =
        List(count) { index -> "word$index" }.joinToString(" ")

    private fun article(
        title: String,
        summary: String,
    ) = NewsCardUiModel(
        id = "article",
        imageUrl = null,
        placeholderImageResId = R.drawable.news_world,
        categoryResId = R.string.category_serbia,
        source = "Test",
        time = "pre 1 min",
        title = title,
        summary = summary,
        url = "https://example.com/article",
        normalizedText = "",
        relatedCityIds = emptySet(),
    )
}
