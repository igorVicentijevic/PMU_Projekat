package com.example.newsagreggator.data.sample

import com.example.newsagreggator.business.repository.ArticleState
import com.example.newsagreggator.business.repository.ArticleStateRepository
import com.example.newsagreggator.business.repository.UserPreferences
import com.example.newsagreggator.business.repository.UserPreferencesRepository
import com.example.newsagreggator.business.service.NetworkMonitor
import com.example.newsagreggator.business.service.NewsRefreshScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class InMemoryArticleStateRepository : ArticleStateRepository {
    private val state = MutableStateFlow(ArticleState())

    override val articleState: Flow<ArticleState> = state

    override suspend fun setArticleSaved(articleId: String, saved: Boolean) {
        state.update { currentState ->
            currentState.copy(
                savedArticleIds = if (saved) {
                    currentState.savedArticleIds + articleId
                } else {
                    currentState.savedArticleIds - articleId
                }
            )
        }
    }

    override suspend fun markArticleRead(articleId: String) {
        state.update { currentState ->
            currentState.copy(
                readArticleIds = currentState.readArticleIds + articleId
            )
        }
    }

    override suspend fun clearReadingHistory() {
        state.update { currentState ->
            currentState.copy(readArticleIds = emptySet())
        }
    }
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

class InMemoryNetworkMonitor(
    initialOnlineState: Boolean = true,
) : NetworkMonitor {
    override val isOnline = MutableStateFlow(initialOnlineState)
}

class InMemoryNewsRefreshScheduler : NewsRefreshScheduler {
    override fun schedule(intervalMinutes: Int) = Unit
}
