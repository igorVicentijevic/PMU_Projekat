package com.example.newsagreggator.toneanalyzer

import com.example.newsagreggator.model.Article
import com.example.newsagreggator.model.NewsCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleToneServiceTest {
    private val service = ArticleToneService(
        analysisStrategy = SampleArticleToneAnalysisStrategy()
    )

    @Test
    fun getTone_returnsStableDistributionThatTotalsOneHundred() = runBlocking {
        val article = Article(
            id = "article-1",
            title = "Naslov",
            summary = "Sadržaj vesti",
            source = "Izvor",
            category = NewsCategory.Serbia,
            publishedAtEpochMillis = 0L,
            imageUrl = null,
            articleUrl = "https://example.com/article-1",
        )

        val first = service.getTone(article)
        val second = service.getTone(article)

        assertEquals(first, second)
        assertEquals(
            100,
            first.negative + first.neutral + first.positive,
        )
    }
}
