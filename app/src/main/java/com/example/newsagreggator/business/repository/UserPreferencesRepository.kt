package com.example.newsagreggator.business.repository

import com.example.newsagreggator.R
import kotlinx.coroutines.flow.Flow

data class UserPreferences(
    val darkThemeOverride: Boolean? = null,
    val compactLayout: Boolean = false,
    val refreshIntervalMinutes: Int = 15,
    val breakingNewsEnabled: Boolean = true,
    val followedCategories: Set<Int> = defaultFollowedCategories,
)

val defaultFollowedCategories: Set<Int> = setOf(
    R.string.category_serbia,
    R.string.category_technology,
    R.string.category_world,
)

interface UserPreferencesRepository {
    val preferences: Flow<UserPreferences>

    suspend fun setDarkThemeOverride(enabled: Boolean)

    suspend fun setCompactLayout(enabled: Boolean)

    suspend fun setRefreshInterval(minutes: Int)

    suspend fun setBreakingNewsEnabled(enabled: Boolean)

    suspend fun setFollowedCategories(categories: Set<Int>)
}
