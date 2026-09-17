package com.example.newsagreggator.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = TokGreenSoft,
    onPrimary = TokGreenDark,
    primaryContainer = TokDarkReadSurface,
    onPrimaryContainer = TokDarkText,
    secondary = TokCoral,
    background = TokDarkBackground,
    onBackground = TokDarkText,
    surface = TokDarkSurface,
    onSurface = TokDarkText,
    onSurfaceVariant = TokDarkMuted,
    outline = TokMuted,
)

private val LightColorScheme = lightColorScheme(
    primary = TokGreen,
    onPrimary = TokSurface,
    primaryContainer = TokReadSurface,
    onPrimaryContainer = TokText,
    secondary = TokCoral,
    background = TokBackground,
    onBackground = TokText,
    surface = TokSurface,
    onSurface = TokText,
    onSurfaceVariant = TokMuted,
    outline = TokOutline,
)

@Composable
fun NewsAgreggatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}