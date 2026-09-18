package com.example.newsagreggator.notification.strategy

import com.example.newsagreggator.model.Article
import com.example.newsagreggator.model.NewsCategory
import com.example.newsagreggator.util.comparator.LevenshteinWordComparator
import com.example.newsagreggator.util.SerbianTextNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeywordBreakingNewsStrategyTest {
    private val now = 1_000_000_000L
    private val strategy = KeywordBreakingNewsStrategy(
        textNormalizer = SerbianTextNormalizer(),
        wordComparator = LevenshteinWordComparator(),
        currentTimeMillis = { now },
    )

    @Test
    fun detectsFreshUrgentEscalation() {
        val decision = strategy.evaluate(
            article(
                title = "HITNO: zatvoren važan put",
                publishedAtEpochMillis = now,
            )
        )

        assertTrue(decision.isBreaking)
        assertEquals(75, decision.score)
        assertTrue("urgency-in-title" in decision.reasons)
        assertTrue("escalation" in decision.reasons)
        assertTrue("published-within-30-minutes" in decision.reasons)
        assertTrue("uppercase-urgency-prefix" in decision.reasons)
    }

    @Test
    fun detectsInflectedHighImpactWordUsingLevenshteinComparison() {
        val decision = strategy.evaluate(
            article(
                title = "Hitno izveštavanje o zemljotresu",
                publishedAtEpochMillis = now,
            )
        )

        assertTrue(decision.isBreaking)
        assertTrue("high-impact-event" in decision.reasons)
    }

    @Test
    fun ignoresRegularArticle() {
        assertFalse(
            strategy.isBreaking(
                article(
                    title = "Otvoren novi gradski park",
                    publishedAtEpochMillis = now,
                )
            )
        )
    }

    @Test
    fun rejectsStaleUrgentArticleWithoutAnotherStrongSignal() {
        val decision = strategy.evaluate(
            article(
                title = "Hitno saopštenje",
                publishedAtEpochMillis = now - 3 * 60 * 60 * 1_000L,
            )
        )

        assertFalse(decision.isBreaking)
        assertEquals(10, decision.score)
        assertTrue("older-than-two-hours" in decision.reasons)
    }

    private fun article(
        title: String,
        publishedAtEpochMillis: Long,
    ) = Article(
        id = title,
        title = title,
        summary = "Test",
        source = "Test",
        category = NewsCategory.Serbia,
        publishedAtEpochMillis = publishedAtEpochMillis,
        imageUrl = null,
        articleUrl = "https://example.com/article",
    )
}
