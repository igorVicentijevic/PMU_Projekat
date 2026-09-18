package com.example.newsagreggator.notification.strategy

import com.example.newsagreggator.model.Article
import com.example.newsagreggator.model.NewsCategory
import com.example.newsagreggator.notification.strategy.KeywordBreakingNewsStrategy
import com.example.newsagreggator.util.SerbianTextNormalizer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KeywordBreakingNewsStrategyTest {
    private val strategy = KeywordBreakingNewsStrategy(
        textNormalizer = SerbianTextNormalizer()
    )

    @Test
    fun detectsBreakingNewsKeyword() {
        assertTrue(
            strategy.isBreaking(
                article("Hitno: zatvoren važan put")
            )
        )
    }

    @Test
    fun ignoresRegularArticle() {
        assertFalse(
            strategy.isBreaking(
                article("Otvoren novi gradski park")
            )
        )
    }

    private fun article(title: String) = Article(
        id = title,
        title = title,
        summary = "Test",
        source = "Test",
        category = NewsCategory.Serbia,
        publishedAtEpochMillis = 0L,
        imageUrl = null,
        articleUrl = "https://example.com/article",
    )
}
