package com.example.newsagreggator.data.notification

import com.example.newsagreggator.business.model.Article
import com.example.newsagreggator.business.model.NewsCategory
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Test

class RandomBreakingNewsStrategyTest {
    @Test
    fun delegatesDecisionToRandomGenerator() {
        val expectedRandom = Random(123)
        val strategy = RandomBreakingNewsStrategy(Random(123))

        repeat(10) {
            assertEquals(
                expectedRandom.nextBoolean(),
                strategy.isBreaking(article),
            )
        }
    }

    private val article = Article(
        id = "test",
        title = "Test",
        summary = "Test",
        source = "Test",
        category = NewsCategory.Serbia,
        publishedAtEpochMillis = 0L,
        imageUrl = null,
        articleUrl = "https://example.com/article",
    )
}
