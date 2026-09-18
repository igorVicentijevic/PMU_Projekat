package com.example.newsagreggator.location

import com.example.newsagreggator.model.Coordinates

interface CurrentLocationProvider {
    suspend fun getCurrentLocation(): CurrentLocationResult
}

sealed interface CurrentLocationResult {
    data class Available(
        val coordinates: Coordinates,
    ) : CurrentLocationResult

    data object PermissionRequired : CurrentLocationResult

    data object LocationServicesDisabled : CurrentLocationResult

    data class Unavailable(
        val cause: Throwable? = null,
    ) : CurrentLocationResult
}
