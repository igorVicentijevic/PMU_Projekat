package com.example.newsagreggator.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsagreggator.data.NewsRepository
import com.example.newsagreggator.data.SampleNewsRepository
import com.example.newsagreggator.ui.state.SecondaryScreen
import com.example.newsagreggator.ui.state.TokTab
import com.example.newsagreggator.ui.state.TokUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TokViewModel(
    private val newsRepository: NewsRepository = SampleNewsRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TokUiState(articles = newsRepository.getArticles())
    )
    val uiState: StateFlow<TokUiState> = _uiState.asStateFlow()

    fun setDarkTheme(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(darkThemeOverride = enabled)
        }
    }

    fun refreshArticles() {
        if (_uiState.value.isRefreshing) return

        _uiState.update { currentState ->
            currentState.copy(isRefreshing = true)
        }
        viewModelScope.launch {
            try {
                val articles = newsRepository.getArticles()
                delay(700)
                _uiState.update { currentState ->
                    currentState.copy(articles = articles)
                }
            } finally {
                _uiState.update { currentState ->
                    currentState.copy(isRefreshing = false)
                }
            }
        }
    }

    fun setCompactLayout(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(compactLayout = enabled)
        }
    }

    fun setRefreshInterval(minutes: Int) {
        _uiState.update { currentState ->
            currentState.copy(refreshIntervalMinutes = minutes)
        }
    }

    fun setBreakingNewsEnabled(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(breakingNewsEnabled = enabled)
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
    }

    fun markArticleRead(articleId: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                readArticleIds = currentState.readArticleIds + articleId
            )
        }
    }

    fun clearReadingHistory() {
        _uiState.update { currentState ->
            currentState.copy(readArticleIds = emptySet())
        }
    }

    fun toggleFollowedCategory(category: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                followedCategories = if (
                    category in currentState.followedCategories
                ) {
                    currentState.followedCategories - category
                } else {
                    currentState.followedCategories + category
                }
            )
        }
    }
}
