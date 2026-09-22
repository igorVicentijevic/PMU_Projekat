package com.example.newsagreggator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsagreggator.pdf.service.ArticlePdfService
import com.example.newsagreggator.ui.elements.TokApp
import com.example.newsagreggator.ui.elements.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.stateholders.AppearanceViewModel
import com.example.newsagreggator.ui.stateholders.TokViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val tokViewModel: TokViewModel by viewModels()
    private val appearanceViewModel: AppearanceViewModel by viewModels()

    @Inject
    lateinit var articlePdfService: ArticlePdfService

    override fun onResume() {
        super.onResume()
        tokViewModel.refreshArticlesIfStale()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by tokViewModel.uiState.collectAsStateWithLifecycle()
            val appearanceState by
                appearanceViewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme =
                appearanceState.darkTheme ?: isSystemInDarkTheme()

            NewsAgreggatorTheme(darkTheme = darkTheme) {
                TokApp(
                    darkTheme = darkTheme,
                    ambientLightSensorAvailable =
                        appearanceState.ambientLightSensorAvailable,
                    tokViewModel = tokViewModel,
                    articlePdfService = articlePdfService,
                )
            }
        }
    }
}
