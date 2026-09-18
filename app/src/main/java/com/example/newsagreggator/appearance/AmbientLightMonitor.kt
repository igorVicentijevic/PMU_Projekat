package com.example.newsagreggator.appearance

import kotlinx.coroutines.flow.Flow

sealed interface AmbientLightReading {
    data object Inactive : AmbientLightReading

    data object Unavailable : AmbientLightReading

    data class Available(
        val illuminanceLux: Float,
    ) : AmbientLightReading
}

interface AmbientLightMonitor {
    val readings: Flow<AmbientLightReading>
}
