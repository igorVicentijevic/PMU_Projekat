package com.example.newsagreggator.business.service

import com.example.newsagreggator.business.model.Coordinates

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
