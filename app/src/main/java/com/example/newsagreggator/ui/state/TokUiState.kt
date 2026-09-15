package com.example.newsagreggator.ui.state

import com.example.newsagreggator.R

enum class SecondaryScreen {
    History,
    Digest,
}

data class TokUiState(
    val compactLayout: Boolean = false,
    val refreshIntervalMinutes: Int = 15,
    val breakingNewsEnabled: Boolean = true,
    val secondaryScreen: SecondaryScreen? = null,
    val savedArticleIds: Set<Int> = emptySet(),
    val readArticleIds: Set<Int> = emptySet(),
    val followedCategories: Set<Int> = setOf(
        R.string.category_serbia,
        R.string.category_technology,
        R.string.category_world,
    ),
)
