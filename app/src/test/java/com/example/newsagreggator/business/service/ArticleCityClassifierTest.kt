package com.example.newsagreggator.business.service

import com.example.newsagreggator.business.model.Article
import com.example.newsagreggator.business.model.NewsCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleCityClassifierTest {
    @Test
    fun delegatesDetectionToConfiguredStrategy() {
        val expectedCityIds = setOf("custom-city")
        val classifier = ArticleCityClassifier(
            strategy = ArticleLocationDetectionStrategy {
                expectedCityIds
            }
        )

        val cityIds = classifier.classify(
            Article(
                id = "test",
                title = "Test",
                summary = "Test",
                source = "Test",
                category = NewsCategory.Serbia,
                publishedAtEpochMillis = 0L,
                imageUrl = null,
                articleUrl = "https://example.com/test",
            )
        )

        assertEquals(expectedCityIds, cityIds)
    }
}
