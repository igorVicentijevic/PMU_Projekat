package com.example.newsagreggator.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.newsagreggator.ui.state.SecondaryScreen
import com.example.newsagreggator.ui.state.TokUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TokViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TokUiState())
    val uiState: StateFlow<TokUiState> = _uiState.asStateFlow()

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
