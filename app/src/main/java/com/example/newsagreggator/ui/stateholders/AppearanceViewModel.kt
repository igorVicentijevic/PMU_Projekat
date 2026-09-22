package com.example.newsagreggator.ui.stateholders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsagreggator.appearance.AmbientLightMonitor
import com.example.newsagreggator.appearance.AmbientLightReading
import com.example.newsagreggator.appearance.AmbientThemeDecider
import com.example.newsagreggator.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn

data class AppearanceUiState(
    val darkTheme: Boolean? = null,
    val ambientLightSensorAvailable: Boolean? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AppearanceViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val ambientLightMonitor: AmbientLightMonitor,
    private val ambientThemeDecider: AmbientThemeDecider,
) : ViewModel() {
    private val themePreferences =
        userPreferencesRepository.preferences
            .map { preferences ->
                ThemePreferences(
                    darkThemeOverride = preferences.darkThemeOverride,
                    automaticThemeEnabled =
                        preferences.automaticThemeEnabled,
                )
            }
            .distinctUntilChanged()

    val uiState: StateFlow<AppearanceUiState> =
        themePreferences
            .flatMapLatest { preferences ->
                if (preferences.automaticThemeEnabled) {
                    automaticThemeStates()
                } else {
                    flowOf(
                        AppearanceUiState(
                            darkTheme = preferences.darkThemeOverride,
                        )
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = 5_000
                ),
                initialValue = AppearanceUiState(),
            )

    private fun automaticThemeStates(): Flow<AppearanceUiState> =
        ambientLightMonitor.readings.scan(AppearanceUiState()) {
                currentState,
                reading,
            ->
            when (reading) {
                is AmbientLightReading.Available ->
                    AppearanceUiState(
                        darkTheme = ambientThemeDecider.shouldUseDarkTheme(
                            illuminanceLux = reading.illuminanceLux,
                            currentlyDark = currentState.darkTheme,
                        ),
                        ambientLightSensorAvailable = true,
                    )

                AmbientLightReading.Unavailable ->
                    AppearanceUiState(
                        ambientLightSensorAvailable = false,
                    )

                AmbientLightReading.Inactive -> AppearanceUiState()
            }
        }

    private data class ThemePreferences(
        val darkThemeOverride: Boolean?,
        val automaticThemeEnabled: Boolean,
    )
}
