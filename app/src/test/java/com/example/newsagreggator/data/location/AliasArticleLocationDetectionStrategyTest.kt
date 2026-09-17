package com.example.newsagreggator.data.location

import com.example.newsagreggator.business.model.Article
import com.example.newsagreggator.business.model.NewsCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AliasArticleLocationDetectionStrategyTest {
    private val strategy = AliasArticleLocationDetectionStrategy(
        cityCatalog = SerbianCityResolver()
    )

    @Test
    fun detectsInflectedMultiWordCityName() {
        val article = article(
            title = "Nova biciklistička staza otvorena u Novom Sadu",
            summary = "Radovi su završeni jutros.",
        )

        assertEquals(
            setOf("novi-sad"),
            strategy.detectRelatedCityIds(article),
        )
    }

    @Test
    fun detectionIgnoresDiacriticsAndCase() {
        val article = article(
            title = "NIS dobija novi park",
            summary = "Radovi počinju sledeće nedelje.",
        )

        assertEquals(
            setOf("nis"),
            strategy.detectRelatedCityIds(article),
        )
    }

    @Test
    fun shortCityNameDoesNotMatchPartOfAnotherWord() {
        val article = article(
            title = "Ništa se neće menjati",
            summary = "Objavljeno je novo saopštenje.",
        )

        assertTrue(strategy.detectRelatedCityIds(article).isEmpty())
    }

    private fun article(
        title: String,
        summary: String,
    ) = Article(
        id = "test",
        title = title,
        summary = summary,
        source = "Test",
        category = NewsCategory.Serbia,
        publishedAtEpochMillis = 0L,
        imageUrl = null,
        articleUrl = "https://example.com/test",
    )
}
