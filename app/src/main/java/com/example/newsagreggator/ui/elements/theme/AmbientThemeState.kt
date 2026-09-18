package com.example.newsagreggator.ui.elements.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.newsagreggator.appearance.AmbientLightMonitor
import com.example.newsagreggator.appearance.AmbientLightReading
import com.example.newsagreggator.appearance.AmbientThemeDecider
import kotlinx.coroutines.flow.flowOf

data class AmbientThemeState(
    val darkTheme: Boolean,
    val sensorAvailable: Boolean?,
)

@Composable
fun rememberAmbientThemeState(
    automaticThemeEnabled: Boolean,
    darkThemeOverride: Boolean?,
    ambientLightMonitor: AmbientLightMonitor,
    ambientThemeDecider: AmbientThemeDecider,
): AmbientThemeState {

    val systemDarkTheme = isSystemInDarkTheme()

    //subscribing to ambientLightFlow
    val readingFlow = remember(automaticThemeEnabled, ambientLightMonitor) {
        if (automaticThemeEnabled) {
            ambientLightMonitor.readings
        } else {
            flowOf(AmbientLightReading.Inactive)
        }
    }

    //collecting readings from flow
    val reading by readingFlow.collectAsStateWithLifecycle(
        initialValue = AmbientLightReading.Inactive
    )


    var ambientDarkTheme by remember {
        mutableStateOf<Boolean?>(null)
    }

    //whenever of these two arguments change, lounch coroutine
    LaunchedEffect(automaticThemeEnabled, reading) {
        ambientDarkTheme = calculateAmbientDarkTheme(
            automaticThemeEnabled = automaticThemeEnabled,
            reading = reading,
            currentlyDark = ambientDarkTheme,
            ambientThemeDecider = ambientThemeDecider,
        )
    }


    return AmbientThemeState(
        darkTheme = resolveDarkTheme(
            automaticThemeEnabled = automaticThemeEnabled,
            ambientDarkTheme = ambientDarkTheme,
            darkThemeOverride = darkThemeOverride,
            systemDarkTheme = systemDarkTheme,
        ),
        sensorAvailable = reading.sensorAvailable(),
    )
}

private fun calculateAmbientDarkTheme(
    automaticThemeEnabled: Boolean,
    reading: AmbientLightReading,
    currentlyDark: Boolean?,
    ambientThemeDecider: AmbientThemeDecider,
): Boolean? {
    if (!automaticThemeEnabled) {
        return null
    }

    return when (reading) {
        is AmbientLightReading.Available ->
            ambientThemeDecider.shouldUseDarkTheme(
                illuminanceLux = reading.illuminanceLux,
                currentlyDark = currentlyDark,
            )

        AmbientLightReading.Inactive,
        AmbientLightReading.Unavailable,
        -> null
    }
}

private fun resolveDarkTheme(
    automaticThemeEnabled: Boolean,
    ambientDarkTheme: Boolean?,
    darkThemeOverride: Boolean?,
    systemDarkTheme: Boolean,
): Boolean =
    if (automaticThemeEnabled) {
        ambientDarkTheme ?: systemDarkTheme
    } else {
        darkThemeOverride ?: systemDarkTheme
    }

private fun AmbientLightReading.sensorAvailable(): Boolean? =
    when (this) {
        is AmbientLightReading.Available -> true
        AmbientLightReading.Unavailable -> false
        AmbientLightReading.Inactive -> null
    }
