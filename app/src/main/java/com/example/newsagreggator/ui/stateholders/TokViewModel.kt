package com.example.newsagreggator.ui.stateholders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsagreggator.articlegrouping.ArticleGroupingService
import com.example.newsagreggator.commands.DetectNearestCityCommand
import com.example.newsagreggator.commands.DetectNearestCityResult
import com.example.newsagreggator.commands.RefreshNewsCommand
import com.example.newsagreggator.commands.ToggleFollowedCategoryCommand
import com.example.newsagreggator.commands.UpdateRefreshIntervalCommand
import com.example.newsagreggator.digest.strategy.DailyDigestStrategy
import com.example.newsagreggator.digest.strategy.DigestReadingTimeStrategy
import com.example.newsagreggator.repository.ArticleStateRepository
import com.example.newsagreggator.repository.NewsRepository
import com.example.newsagreggator.repository.SelectedCityRepository
import com.example.newsagreggator.repository.UserPreferencesRepository
import com.example.newsagreggator.toneanalyzer.ArticleToneService
import com.example.newsagreggator.location.CityResolver
import com.example.newsagreggator.network.NetworkMonitor
import com.example.newsagreggator.background.NewsRefreshScheduler
import com.example.newsagreggator.util.TextNormalizer
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
    private val dailyDigestStrategy: DailyDigestStrategy,
    private val digestReadingTimeStrategy: DigestReadingTimeStrategy,
    private val articleToneService: ArticleToneService,
    private val articleGroupingService: ArticleGroupingService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TokUiState())
    val uiState: StateFlow<TokUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            newsRepository.news.collect { newsSnapshot ->
                val articleGroups =
                    articleGroupingService.getGroups(newsSnapshot.articles)
                val articles = newsSnapshot.articles.map { article ->
                    article.toNewsCardUiModel(
                        toneDistribution = articleToneService.getTone(article)
                    )
                }
                val articlesById = articles.associateBy(NewsCardUiModel::id)
                val articleGroupUiModels = articleGroups.map { group ->
                    ArticleGroupUiModel(
                        id = group.id,
                        title = group.title,
                        articles = group.articles.map { article ->
                            articlesById[article.id]
                                ?: article.toNewsCardUiModel(
                                    toneDistribution =
                                        articleToneService.getTone(article)
                                )
                        },
                    )
                }
                _uiState.update { currentState ->
                    val digestArticles = dailyDigestStrategy.select(
                        articles = articles,
                        followedCategories =
                            currentState.followedCategories,
                    )
                    currentState.copy(
                        articles = articles,
                        articleGroups = articleGroupUiModels,
                        digestArticles = digestArticles,
                        digestReadingTimeMinutes =
                            digestReadingTimeStrategy.estimateMinutes(
                                digestArticles
                            ),
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
                    val digestArticles = dailyDigestStrategy.select(
                        articles = currentState.articles,
                        followedCategories =
                            preferences.followedCategories,
                    )
                    currentState.copy(
                        darkThemeOverride = preferences.darkThemeOverride,
                        automaticThemeEnabled =
                            preferences.automaticThemeEnabled,
                        compactLayout = preferences.compactLayout,
                        refreshIntervalMinutes = refreshIntervalMinutes,
                        breakingNewsEnabled = preferences.breakingNewsEnabled,
                        followedCategories = preferences.followedCategories,
                        digestArticles = digestArticles,
                        digestReadingTimeMinutes =
                            digestReadingTimeStrategy.estimateMinutes(
                                digestArticles
                            ),
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

    fun setAutomaticThemeEnabled(enabled: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(automaticThemeEnabled = enabled)
        }
        viewModelScope.launch {
            userPreferencesRepository.setAutomaticThemeEnabled(enabled)
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

    fun selectForYouTab(tab: ForYouTab) {
        _uiState.update { currentState ->
            currentState.copy(selectedForYouTab = tab)
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
            val digestArticles = dailyDigestStrategy.select(
                articles = currentState.articles,
                followedCategories = followedCategories,
            )
            currentState.copy(
                followedCategories = followedCategories,
                digestArticles = digestArticles,
                digestReadingTimeMinutes =
                    digestReadingTimeStrategy.estimateMinutes(
                        digestArticles
                    ),
            )
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
