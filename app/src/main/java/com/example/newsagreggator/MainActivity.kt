package com.example.newsagreggator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.newsagreggator.data.SampleNewsRepository
import com.example.newsagreggator.ui.elements.TokApp
import com.example.newsagreggator.ui.theme.NewsAgreggatorTheme
import com.example.newsagreggator.ui.viewmodel.TokViewModel
import com.example.newsagreggator.ui.viewmodel.tokViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModelFactory = remember {
                tokViewModelFactory(SampleNewsRepository())
            }
            val tokViewModel: TokViewModel = viewModel(
                factory = viewModelFactory
            )
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
