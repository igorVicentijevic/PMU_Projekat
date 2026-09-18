package com.example.newsagreggator.appearance

import javax.inject.Inject

class AmbientThemeDecider @Inject constructor() {
    fun shouldUseDarkTheme(
        illuminanceLux: Float,
        currentlyDark: Boolean?,
    ): Boolean = when (currentlyDark) {
        true -> illuminanceLux < LIGHT_THEME_THRESHOLD_LUX
        false -> illuminanceLux <= DARK_THEME_THRESHOLD_LUX
        null -> illuminanceLux <= DARK_THEME_THRESHOLD_LUX
    }

    companion object {
        const val DARK_THEME_THRESHOLD_LUX = 50f
        const val LIGHT_THEME_THRESHOLD_LUX = 100f
    }
}
