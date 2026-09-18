package com.example.newsagreggator.data.sample

import com.example.newsagreggator.repository.ArticleState
import com.example.newsagreggator.repository.ArticleStateRepository
import com.example.newsagreggator.repository.SelectedCityRepository
import com.example.newsagreggator.repository.UserPreferences
import com.example.newsagreggator.repository.UserPreferencesRepository
import com.example.newsagreggator.model.City
import com.example.newsagreggator.model.Coordinates
import com.example.newsagreggator.location.CityResolver
import com.example.newsagreggator.location.CurrentLocationProvider
import com.example.newsagreggator.location.CurrentLocationResult
import com.example.newsagreggator.network.NetworkMonitor
import com.example.newsagreggator.background.NewsRefreshScheduler
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

    override suspend fun setAutomaticThemeEnabled(enabled: Boolean) {
        state.update { it.copy(automaticThemeEnabled = enabled) }
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

class InMemorySelectedCityRepository(
    initialCityId: String? = null,
) : SelectedCityRepository {
    private val selectedCity = MutableStateFlow(initialCityId)
    override val selectedCityId: Flow<String?> = selectedCity

    override suspend fun setSelectedCity(cityId: String) {
        selectedCity.value = cityId
    }

    override suspend fun clearSelectedCity() {
        selectedCity.value = null
    }
}

class InMemoryCurrentLocationProvider(
    private val result: CurrentLocationResult =
        CurrentLocationResult.Available(BELGRADE.coordinates),
) : CurrentLocationProvider {
    override suspend fun getCurrentLocation(): CurrentLocationResult = result
}

class InMemoryCityResolver : CityResolver {
    override fun findNearestCity(coordinates: Coordinates): City = BELGRADE

    override fun findCityById(cityId: String): City? =
        BELGRADE.takeIf { city -> city.id == cityId }
}

private val BELGRADE = City(
    id = "belgrade",
    name = "Beograd",
    coordinates = Coordinates(
        latitude = 44.7866,
        longitude = 20.4489,
    ),
    aliases = setOf("Beograd", "Beogradu", "beogradski"),
)
