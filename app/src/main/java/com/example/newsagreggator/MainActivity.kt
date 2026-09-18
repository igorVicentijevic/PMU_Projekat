package com.example.newsagreggator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsagreggator.appearance.AmbientLightMonitor
import com.example.newsagreggator.appearance.AmbientThemeDecider
import com.example.newsagreggator.ui.elements.TokApp
import com.example.newsagreggator.ui.elements.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.elements.theme.rememberAmbientThemeState
import com.example.newsagreggator.ui.stateholders.TokViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val tokViewModel: TokViewModel by viewModels()

    @Inject
    lateinit var ambientLightMonitor: AmbientLightMonitor

    @Inject
    lateinit var ambientThemeDecider: AmbientThemeDecider

    override fun onResume() {
        super.onResume()
        tokViewModel.refreshArticlesIfStale()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by tokViewModel.uiState.collectAsStateWithLifecycle()
            val themeState = rememberAmbientThemeState(
                automaticThemeEnabled = uiState.automaticThemeEnabled,
                darkThemeOverride = uiState.darkThemeOverride,
                ambientLightMonitor = ambientLightMonitor,
                ambientThemeDecider = ambientThemeDecider,
            )

            NewsAgreggatorTheme(darkTheme = themeState.darkTheme) {
                TokApp(
                    darkTheme = themeState.darkTheme,
                    ambientLightSensorAvailable =
                        themeState.sensorAvailable,
                    tokViewModel = tokViewModel,
                )
            }
        }
    }
}
