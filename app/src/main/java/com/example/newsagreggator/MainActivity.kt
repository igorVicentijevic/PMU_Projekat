package com.example.newsagreggator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.newsagreggator.ui.elements.TokApp
import com.example.newsagreggator.ui.theme.NewsAgreggatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            var darkThemeOverride by rememberSaveable {
                mutableStateOf<Boolean?>(null)
            }
            val darkTheme = darkThemeOverride ?: systemDarkTheme

            NewsAgreggatorTheme(darkTheme = darkTheme) {
                TokApp(
                    darkTheme = darkTheme,
                    onDarkThemeChange = { darkThemeOverride = it },
                )
            }
        }
    }
}
