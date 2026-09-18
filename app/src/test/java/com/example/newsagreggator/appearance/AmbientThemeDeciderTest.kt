package com.example.newsagreggator.appearance

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AmbientThemeDeciderTest {
    private val decider = AmbientThemeDecider()

    @Test
    fun enablesDarkThemeAtFiftyLux() {
        assertTrue(
            decider.shouldUseDarkTheme(
                illuminanceLux = 50f,
                currentlyDark = false,
            )
        )
    }

    @Test
    fun keepsDarkThemeBetweenThresholds() {
        assertTrue(
            decider.shouldUseDarkTheme(
                illuminanceLux = 75f,
                currentlyDark = true,
            )
        )
    }

    @Test
    fun keepsLightThemeBetweenThresholds() {
        assertFalse(
            decider.shouldUseDarkTheme(
                illuminanceLux = 75f,
                currentlyDark = false,
            )
        )
    }

    @Test
    fun enablesLightThemeAtOneHundredLux() {
        assertFalse(
            decider.shouldUseDarkTheme(
                illuminanceLux = 100f,
                currentlyDark = true,
            )
        )
    }
}
