package com.example.newsagreggator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsagreggator.data.NewsRepository
import com.example.newsagreggator.data.local.ArticleStateRepository
import com.example.newsagreggator.data.preferences.UserPreferencesRepository
import com.example.newsagreggator.ui.state.SecondaryScreen
import com.example.newsagreggator.ui.state.TokTab
import com.example.newsagreggator.ui.state.TokUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TokViewModel(
    private val newsRepository: NewsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleStateRepository: ArticleStateRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TokUiState(articles = newsRepository.getArticles())
    )
    val uiState: StateFlow<TokUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.preferences.collect { preferences ->
                _uiState.update { currentState ->
                    currentState.copy(
                        darkThemeOverride = preferences.darkThemeOverride,
                        compactLayout = preferences.compactLayout,
                        refreshIntervalMinutes = preferences.refreshIntervalMinutes,
                        breakingNewsEnabled = preferences.breakingNewsEnabled,
                        followedCategories = preferences.followedCategories,
                    )
                }
            }
        }
        viewModelScope.launch {
            articleStateRepository.articleState.collect { articleState ->
                _uiState.update { currentState ->
                    currentState.copy(
                        savedArticleIds = articleState.savedArticleIds,
                        readArticleIds = articleState.readArticleIds,
                    )
                }
            }
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(darkThemeOverride = enabled)
        }
        viewModelScope.launch {
            userPreferencesRepository.setDarkThemeOverride(enabled)
        }
    }

    fun refreshArticles() {
        if (_uiState.value.isRefreshing) return

        _uiState.update { currentState ->
            currentState.copy(
                isRefreshing = true,
                articleRefreshFailed = false,
            )
        }
        viewModelScope.launch {
            try {
                newsRepository.refreshArticles().fold(
                    onSuccess = { articles ->
                        _uiState.update { currentState ->
                            currentState.copy(articles = articles)
                        }
                    },
                    onFailure = {
                        _uiState.update { currentState ->
                            currentState.copy(articleRefreshFailed = true)
                        }
                    },
                )
            } finally {
                _uiState.update { currentState ->
                    currentState.copy(isRefreshing = false)
                }
            }
        }
    }

    fun clearArticleRefreshError() {
        _uiState.update { currentState ->
            currentState.copy(articleRefreshFailed = false)
        }
    }

    fun setCompactLayout(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(compactLayout = enabled)
        }
        viewModelScope.launch {
            userPreferencesRepository.setCompactLayout(enabled)
        }
    }

    fun setRefreshInterval(minutes: Int) {
        _uiState.update { currentState ->
            currentState.copy(refreshIntervalMinutes = minutes)
        }
        viewModelScope.launch {
            userPreferencesRepository.setRefreshInterval(minutes)
        }
    }

    fun setBreakingNewsEnabled(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(breakingNewsEnabled = enabled)
        }
        viewModelScope.launch {
            userPreferencesRepository.setBreakingNewsEnabled(enabled)
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(searchQuery = query)
        }
    }

    fun selectCategory(category: Int) {
        _uiState.update { currentState ->
            currentState.copy(selectedCategory = category)
        }
    }

    fun selectTab(tab: TokTab) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedTab = tab,
                secondaryScreen = null,
            )
        }
    }

    fun openSecondaryScreen(screen: SecondaryScreen) {
        _uiState.update { currentState ->
            currentState.copy(secondaryScreen = screen)
        }
    }

    fun closeSecondaryScreen() {
        _uiState.update { currentState ->
            currentState.copy(secondaryScreen = null)
        }
    }

    fun setArticleSaved(articleId: Int, saved: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                savedArticleIds = if (saved) {
                    currentState.savedArticleIds + articleId
                } else {
                    currentState.savedArticleIds - articleId
                }
            )
        }
        viewModelScope.launch {
            articleStateRepository.setArticleSaved(articleId, saved)
        }
    }

    fun markArticleRead(articleId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                readArticleIds = currentState.readArticleIds + articleId
            )
        }
        viewModelScope.launch {
            articleStateRepository.markArticleRead(articleId)
        }
    }

    fun clearReadingHistory() {
        _uiState.update { currentState ->
            currentState.copy(readArticleIds = emptySet())
        }
        viewModelScope.launch {
            articleStateRepository.clearReadingHistory()
        }
    }

    fun toggleFollowedCategory(category: Int) {
        val followedCategories = if (
            category in _uiState.value.followedCategories
        ) {
            _uiState.value.followedCategories - category
        } else {
            _uiState.value.followedCategories + category
        }
        _uiState.update { currentState ->
            currentState.copy(followedCategories = followedCategories)
        }
        viewModelScope.launch {
            userPreferencesRepository.setFollowedCategories(followedCategories)
        }
    }
}
