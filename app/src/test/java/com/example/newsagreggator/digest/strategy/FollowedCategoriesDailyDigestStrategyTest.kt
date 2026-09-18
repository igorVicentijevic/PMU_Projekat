package com.example.newsagreggator.digest.strategy

import com.example.newsagreggator.R
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FollowedCategoriesDailyDigestStrategyTest {
    private val strategy = FollowedCategoriesDailyDigestStrategy()

    @Test
    fun selectsFirstFiveArticlesFromFollowedCategoriesInOriginalOrder() {
        val articles = listOf(
            article("1", R.string.category_serbia),
            article("excluded", R.string.category_sport),
            article("2", R.string.category_technology),
            article("3", R.string.category_serbia),
            article("4", R.string.category_technology),
            article("5", R.string.category_serbia),
            article("limited", R.string.category_technology),
        )

        val selected = strategy.select(
            articles = articles,
            followedCategories = setOf(
                R.string.category_serbia,
                R.string.category_technology,
            ),
        )

        assertEquals(listOf("1", "2", "3", "4", "5"), selected.map { it.id })
    }

    @Test
    fun returnsEmptyDigestWhenNoCategoriesAreFollowed() {
        val selected = strategy.select(
            articles = listOf(article("1", R.string.category_serbia)),
            followedCategories = emptySet(),
        )

        assertTrue(selected.isEmpty())
    }

    private fun article(
        id: String,
        categoryResId: Int,
    ) = NewsCardUiModel(
        id = id,
        imageUrl = null,
        placeholderImageResId = R.drawable.news_world,
        categoryResId = categoryResId,
        source = "Test",
        time = "pre 1 min",
        title = "Title $id",
        summary = "Summary $id",
        url = "https://example.com/$id",
        normalizedText = "",
        relatedCityIds = emptySet(),
    )
}
