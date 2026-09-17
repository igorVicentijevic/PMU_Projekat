package com.example.newsagreggator.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsagreggator.business.command.DetectNearestCityCommand
import com.example.newsagreggator.business.command.DetectNearestCityResult
import com.example.newsagreggator.business.command.RefreshNewsCommand
import com.example.newsagreggator.business.command.ToggleFollowedCategoryCommand
import com.example.newsagreggator.business.command.UpdateRefreshIntervalCommand
import com.example.newsagreggator.business.repository.ArticleStateRepository
import com.example.newsagreggator.business.repository.NewsRepository
import com.example.newsagreggator.business.repository.SelectedCityRepository
import com.example.newsagreggator.business.repository.UserPreferencesRepository
import com.example.newsagreggator.business.service.CityResolver
import com.example.newsagreggator.business.service.NetworkMonitor
import com.example.newsagreggator.business.service.NewsRefreshScheduler
import com.example.newsagreggator.business.service.TextNormalizer
import com.example.newsagreggator.presentation.model.toNewsCardUiModel
import com.example.newsagreggator.presentation.state.LocationUiState
import com.example.newsagreggator.presentation.state.SecondaryScreen
import com.example.newsagreggator.presentation.state.TokTab
import com.example.newsagreggator.presentation.state.TokUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TokViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val articleStateRepository: ArticleStateRepository,
    private val networkMonitor: NetworkMonitor,
    private val newsRefreshScheduler: NewsRefreshScheduler,
    private val refreshNewsCommand: RefreshNewsCommand,
    private val updateRefreshIntervalCommand: UpdateRefreshIntervalCommand,
    private val toggleFollowedCategoryCommand: ToggleFollowedCategoryCommand,
    private val detectNearestCityCommand: DetectNearestCityCommand,
    private val selectedCityRepository: SelectedCityRepository,
    private val cityResolver: CityResolver,
    private val textNormalizer: TextNormalizer,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TokUiState())
    val uiState: StateFlow<TokUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            newsRepository.news.collect { newsSnapshot ->
                _uiState.update { currentState ->
                    currentState.copy(
                        articles = newsSnapshot.articles.map {
                            it.toNewsCardUiModel()
                        },
                        lastSuccessfulRefreshEpochMillis =
                            newsSnapshot.lastSuccessfulRefreshEpochMillis,
                    )
                }
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.preferences.collect { preferences ->
                val refreshIntervalMinutes =
                    updateRefreshIntervalCommand.normalize(
                        preferences.refreshIntervalMinutes
                    )
                _uiState.update { currentState ->
                    currentState.copy(
                        darkThemeOverride = preferences.darkThemeOverride,
                        compactLayout = preferences.compactLayout,
                        refreshIntervalMinutes = refreshIntervalMinutes,
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
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                val wasOnline = _uiState.value.hasInternetConnection
                _uiState.update { currentState ->
                    currentState.copy(hasInternetConnection = isOnline)
                }
                if (isOnline && !wasOnline) {
                    refreshArticlesIfStale()
                }
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.preferences
                .map { preferences ->
                    updateRefreshIntervalCommand.normalize(
                        preferences.refreshIntervalMinutes
                    )
                }
                .distinctUntilChanged()
                .collect { refreshIntervalMinutes ->
                    newsRefreshScheduler.schedule(refreshIntervalMinutes)
                }
        }
        viewModelScope.launch {
            selectedCityRepository.selectedCityId
                .distinctUntilChanged()
                .collect { selectedCityId ->
                    val city = selectedCityId?.let(cityResolver::findCityById)
                    if (city != null) {
                        _uiState.update { currentState ->
                            currentState.copy(
                                location = LocationUiState.Selected(
                                    cityId = city.id,
                                    cityName = city.name,
                                )
                            )
                        }
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
        launchArticleRefresh {
            refreshNewsCommand(RefreshNewsCommand.Policy.Force)
        }
    }

    fun refreshArticlesIfStale() {
        val lastSuccessfulRefresh =
            _uiState.value.lastSuccessfulRefreshEpochMillis

        if (refreshNewsCommand.shouldRefresh(lastSuccessfulRefresh)) {
            launchArticleRefresh {
                refreshNewsCommand(RefreshNewsCommand.Policy.IfStale)
            }
        }
    }

    private fun launchArticleRefresh(
        refresh: suspend () -> Result<Unit>,
    ) {
        if (
            _uiState.value.isRefreshing ||
            !_uiState.value.hasInternetConnection
        ) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(
                isRefreshing = true,
                articleRefreshFailed = false,
            )
        }
        viewModelScope.launch {
            try {
                refresh().fold(
                    onSuccess = {},
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
        val validInterval = updateRefreshIntervalCommand.normalize(minutes)
        _uiState.update { currentState ->
            currentState.copy(refreshIntervalMinutes = validInterval)
        }
        viewModelScope.launch {
            updateRefreshIntervalCommand(minutes)
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
            currentState.copy(
                searchQuery = query,
                normalizedSearchQuery = textNormalizer.normalize(query),
            )
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

    fun setArticleSaved(articleId: String, saved: Boolean) {
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

    fun markArticleRead(articleId: String) {
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
        val input = ToggleFollowedCategoryCommand.Input(
            followedCategories = _uiState.value.followedCategories,
            category = category,
        )
        val followedCategories = toggleFollowedCategoryCommand.toggle(input)
        _uiState.update { currentState ->
            currentState.copy(followedCategories = followedCategories)
        }
        viewModelScope.launch {
            toggleFollowedCategoryCommand(input)
        }
    }

    fun detectNearestCity() {
        if (_uiState.value.location == LocationUiState.Detecting) {
            return
        }

        _uiState.update { currentState ->
            currentState.copy(location = LocationUiState.Detecting)
        }
        viewModelScope.launch {
            val locationState = when (val result = detectNearestCityCommand(Unit)) {
                is DetectNearestCityResult.Detected ->
                    LocationUiState.Selected(
                        cityId = result.city.id,
                        cityName = result.city.name,
                    )

                DetectNearestCityResult.PermissionRequired ->
                    LocationUiState.PermissionRequired

                DetectNearestCityResult.LocationServicesDisabled ->
                    LocationUiState.LocationServicesDisabled

                is DetectNearestCityResult.LocationUnavailable,
                is DetectNearestCityResult.Failed,
                -> LocationUiState.LocationUnavailable

                is DetectNearestCityResult.NoSupportedCity ->
                    LocationUiState.OutsideSupportedArea
            }
            _uiState.update { currentState ->
                currentState.copy(location = locationState)
            }
        }
    }

    fun onLocationPermissionDenied() {
        _uiState.update { currentState ->
            currentState.copy(location = LocationUiState.PermissionDenied)
        }
    }
}
