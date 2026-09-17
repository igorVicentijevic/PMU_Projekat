package com.example.newsagreggator.presentation.state

import com.example.newsagreggator.R
import com.example.newsagreggator.business.repository.defaultFollowedCategories
import com.example.newsagreggator.presentation.model.NewsCardUiModel

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

sealed interface LocationUiState {
    data object NotConfigured : LocationUiState

    data object Detecting : LocationUiState

    data object PermissionRequired : LocationUiState

    data object PermissionDenied : LocationUiState

    data object LocationServicesDisabled : LocationUiState

    data object LocationUnavailable : LocationUiState

    data object OutsideSupportedArea : LocationUiState

    data class Selected(
        val cityId: String,
        val cityName: String,
    ) : LocationUiState
}

data class TokUiState(
    val articles: List<NewsCardUiModel> = emptyList(),
    val isRefreshing: Boolean = false,
    val articleRefreshFailed: Boolean = false,
    val lastSuccessfulRefreshEpochMillis: Long? = null,
    val hasInternetConnection: Boolean = true,
    val darkThemeOverride: Boolean? = null,
    val compactLayout: Boolean = false,
    val refreshIntervalMinutes: Int = 15,
    val breakingNewsEnabled: Boolean = true,
    val searchQuery: String = "",
    val normalizedSearchQuery: String = "",
    val selectedCategory: Int = R.string.category_all,
    val selectedTab: TokTab = TokTab.Home,
    val secondaryScreen: SecondaryScreen? = null,
    val savedArticleIds: Set<String> = emptySet(),
    val readArticleIds: Set<String> = emptySet(),
    val followedCategories: Set<Int> = defaultFollowedCategories,
    val location: LocationUiState = LocationUiState.NotConfigured,
)
