package com.example.newsagreggator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newsagreggator.ui.elements.TokApp
import com.example.newsagreggator.ui.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.viewmodel.TokViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val tokViewModel: TokViewModel = viewModel()
            val uiState by tokViewModel.uiState.collectAsStateWithLifecycle()
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = uiState.darkThemeOverride ?: systemDarkTheme

            NewsAgreggatorTheme(darkTheme = darkTheme) {
                TokApp(
                    darkTheme = darkTheme,
                    tokViewModel = tokViewModel,
                )
            }
        }
    }
}
