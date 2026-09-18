package com.example.newsagreggator.location

import com.example.newsagreggator.model.Article
import com.example.newsagreggator.model.NewsCategory
import com.example.newsagreggator.location.AliasArticleLocationDetectionStrategy
import com.example.newsagreggator.location.SerbianCityResolver
import com.example.newsagreggator.util.SerbianTextNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AliasArticleLocationDetectionStrategyTest {
    private val textNormalizer = SerbianTextNormalizer()
    private val strategy = AliasArticleLocationDetectionStrategy(
        cityCatalog = SerbianCityResolver(),
        textNormalizer = textNormalizer,
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

    @Test
    fun detectsCityWrittenInCyrillic() {
        val article = article(
            title = "Нова бициклистичка стаза отворена у Новом Саду",
            summary = "Радови су завршени јутрос.",
        )

        assertEquals(
            setOf("novi-sad"),
            strategy.detectRelatedCityIds(article),
        )
    }

    @Test
    fun matchesLatinAliasAgainstCyrillicArticle() {
        val article = article(
            title = "Ниш добија нови градски парк",
            summary = "Изградња почиње следеће недеље.",
        )

        assertEquals(
            setOf("nis"),
            strategy.detectRelatedCityIds(article),
        )
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
        normalizedText = textNormalizer.normalize("$title $summary Test"),
    )
}
