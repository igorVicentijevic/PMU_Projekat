package com.example.newsagreggator.commands

import com.example.newsagreggator.model.City
import com.example.newsagreggator.model.Coordinates
import com.example.newsagreggator.repository.SelectedCityRepository
import com.example.newsagreggator.location.CityResolver
import com.example.newsagreggator.location.CurrentLocationProvider
import com.example.newsagreggator.location.CurrentLocationResult
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class DetectNearestCityCommand @Inject constructor(
    private val currentLocationProvider: CurrentLocationProvider,
    private val cityResolver: CityResolver,
    private val selectedCityRepository: SelectedCityRepository,
) : Command<Unit, DetectNearestCityResult>() {
    override suspend fun invoke(input: Unit): DetectNearestCityResult {
        return when (
            val location = currentLocationProvider.getCurrentLocation()
        ) {
            is CurrentLocationResult.Available ->
                detectAndSaveCity(location.coordinates)

            CurrentLocationResult.PermissionRequired ->
                DetectNearestCityResult.PermissionRequired

            CurrentLocationResult.LocationServicesDisabled ->
                DetectNearestCityResult.LocationServicesDisabled

            is CurrentLocationResult.Unavailable ->
                DetectNearestCityResult.LocationUnavailable(location.cause)
        }
    }

    private suspend fun detectAndSaveCity(
        coordinates: Coordinates,
    ): DetectNearestCityResult {
        val city = cityResolver.findNearestCity(coordinates)
            ?: return DetectNearestCityResult.NoSupportedCity(coordinates)

        return try {
            selectedCityRepository.setSelectedCity(city.id)
            DetectNearestCityResult.Detected(city)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            DetectNearestCityResult.Failed(error)
        }
    }
}

sealed interface DetectNearestCityResult {
    data class Detected(
        val city: City,
    ) : DetectNearestCityResult

    data object PermissionRequired : DetectNearestCityResult

    data object LocationServicesDisabled : DetectNearestCityResult

    data class LocationUnavailable(
        val cause: Throwable?,
    ) : DetectNearestCityResult

    data class NoSupportedCity(
        val coordinates: Coordinates,
    ) : DetectNearestCityResult

    data class Failed(
        val cause: Throwable,
    ) : DetectNearestCityResult
}
