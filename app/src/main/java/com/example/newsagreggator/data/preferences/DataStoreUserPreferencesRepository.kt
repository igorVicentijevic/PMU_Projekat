package com.example.newsagreggator.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.newsagreggator.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore by preferencesDataStore(
    name = "user_preferences"
)

class DataStoreUserPreferencesRepository(
    context: Context,
) : UserPreferencesRepository {
    private val dataStore = context.applicationContext.userPreferencesDataStore

    override val preferences: Flow<UserPreferences> = dataStore.data.map { values ->
        UserPreferences(
            darkThemeOverride = values[Keys.DarkTheme],
            compactLayout = values[Keys.CompactLayout] ?: false,
            refreshIntervalMinutes = values[Keys.RefreshIntervalMinutes] ?: 15,
            breakingNewsEnabled = values[Keys.BreakingNewsEnabled] ?: true,
            followedCategories = values[Keys.FollowedCategories]
                ?.mapNotNull(categoryResIdByKey::get)
                ?.toSet()
                ?: defaultFollowedCategories,
        )
    }

    override suspend fun setDarkThemeOverride(enabled: Boolean) {
        dataStore.edit { values ->
            values[Keys.DarkTheme] = enabled
        }
    }

    override suspend fun setCompactLayout(enabled: Boolean) {
        dataStore.edit { values ->
            values[Keys.CompactLayout] = enabled
        }
    }

    override suspend fun setRefreshInterval(minutes: Int) {
        dataStore.edit { values ->
            values[Keys.RefreshIntervalMinutes] = minutes
        }
    }

    override suspend fun setBreakingNewsEnabled(enabled: Boolean) {
        dataStore.edit { values ->
            values[Keys.BreakingNewsEnabled] = enabled
        }
    }

    override suspend fun setFollowedCategories(categories: Set<Int>) {
        dataStore.edit { values ->
            values[Keys.FollowedCategories] = categories
                .mapNotNull(categoryKeyByResId::get)
                .toSet()
        }
    }

    private object Keys {
        val DarkTheme = booleanPreferencesKey("dark_theme")
        val CompactLayout = booleanPreferencesKey("compact_layout")
        val RefreshIntervalMinutes = intPreferencesKey("refresh_interval_minutes")
        val BreakingNewsEnabled = booleanPreferencesKey("breaking_news_enabled")
        val FollowedCategories = stringSetPreferencesKey("followed_categories")
    }
}

private val categoryKeyByResId = mapOf(
    R.string.category_serbia to "serbia",
    R.string.category_world to "world",
    R.string.category_technology to "technology",
    R.string.category_business to "business",
    R.string.category_culture to "culture",
    R.string.category_sport to "sport",
    R.string.category_health to "health",
)

private val categoryResIdByKey = categoryKeyByResId.entries.associate {
    (resId, key) -> key to resId
}
