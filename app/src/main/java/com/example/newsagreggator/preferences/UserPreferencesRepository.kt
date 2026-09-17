package com.example.newsagreggator.preferences

import com.example.newsagreggator.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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

class InMemoryUserPreferencesRepository : UserPreferencesRepository {
    private val state = MutableStateFlow(UserPreferences())

    override val preferences: Flow<UserPreferences> = state

    override suspend fun setDarkThemeOverride(enabled: Boolean) {
        state.update { it.copy(darkThemeOverride = enabled) }
    }

    override suspend fun setCompactLayout(enabled: Boolean) {
        state.update { it.copy(compactLayout = enabled) }
    }

    override suspend fun setRefreshInterval(minutes: Int) {
        state.update { it.copy(refreshIntervalMinutes = minutes) }
    }

    override suspend fun setBreakingNewsEnabled(enabled: Boolean) {
        state.update { it.copy(breakingNewsEnabled = enabled) }
    }

    override suspend fun setFollowedCategories(categories: Set<Int>) {
        state.update { it.copy(followedCategories = categories) }
    }
}
