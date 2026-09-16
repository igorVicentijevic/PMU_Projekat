package com.example.newsagreggator.ui.state

import com.example.newsagreggator.R
import com.example.newsagreggator.ui.model.NewsCardUiModel

enum class SecondaryScreen {
    History,
    Digest,
}

enum class TokTab {
    Home,
    ForYou,
    Saved,
    Settings,
}

data class TokUiState(
    val articles: List<NewsCardUiModel> = emptyList(),
    val isRefreshing: Boolean = false,
    val darkThemeOverride: Boolean? = null,
    val compactLayout: Boolean = false,
    val refreshIntervalMinutes: Int = 15,
    val breakingNewsEnabled: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: Int = R.string.category_all,
    val selectedTab: TokTab = TokTab.Home,
    val secondaryScreen: SecondaryScreen? = null,
    val savedArticleIds: Set<Int> = emptySet(),
    val readArticleIds: Set<Int> = emptySet(),
    val followedCategories: Set<Int> = setOf(
        R.string.category_serbia,
        R.string.category_technology,
        R.string.category_world,
    ),
)
