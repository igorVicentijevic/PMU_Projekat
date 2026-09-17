package com.example.newsagreggator.business.command

import com.example.newsagreggator.business.model.City
import com.example.newsagreggator.business.model.Coordinates
import com.example.newsagreggator.business.repository.SelectedCityRepository
import com.example.newsagreggator.business.service.CityResolver
import com.example.newsagreggator.business.service.CurrentLocationProvider
import com.example.newsagreggator.business.service.CurrentLocationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class DetectNearestCityCommandTest {
    private val coordinates = Coordinates(
        latitude = 44.7866,
        longitude = 20.4489,
    )
    private val city = City(
        id = "belgrade",
        name = "Beograd",
        coordinates = coordinates,
        aliases = setOf("Beograd", "Beogradu", "beogradski"),
    )

    @Test
    fun availableLocationDetectsAndSavesNearestCity() = runBlocking {
        val repository = FakeSelectedCityRepository()
        val command = createCommand(
            locationResult = CurrentLocationResult.Available(coordinates),
            resolvedCity = city,
            repository = repository,
        )

        val result = command(Unit)

        assertEquals(DetectNearestCityResult.Detected(city), result)
        assertEquals(city.id, repository.savedCityId)
    }

    @Test
    fun permissionRequiredDoesNotSaveCity() = runBlocking {
        val repository = FakeSelectedCityRepository()
        val command = createCommand(
            locationResult = CurrentLocationResult.PermissionRequired,
            resolvedCity = city,
            repository = repository,
        )

        val result = command(Unit)

        assertSame(DetectNearestCityResult.PermissionRequired, result)
        assertNull(repository.savedCityId)
    }

    @Test
    fun unsupportedLocationReturnsCoordinatesWithoutSavingCity() = runBlocking {
        val repository = FakeSelectedCityRepository()
        val command = createCommand(
            locationResult = CurrentLocationResult.Available(coordinates),
            resolvedCity = null,
            repository = repository,
        )

        val result = command(Unit)

        assertEquals(
            DetectNearestCityResult.NoSupportedCity(coordinates),
            result,
        )
        assertNull(repository.savedCityId)
    }

    @Test
    fun persistenceFailureIsReturnedExplicitly() = runBlocking {
        val error = IllegalStateException("Could not save city")
        val repository = FakeSelectedCityRepository(saveError = error)
        val command = createCommand(
            locationResult = CurrentLocationResult.Available(coordinates),
            resolvedCity = city,
            repository = repository,
        )

        val result = command(Unit)

        assertEquals(DetectNearestCityResult.Failed(error), result)
    }

    private fun createCommand(
        locationResult: CurrentLocationResult,
        resolvedCity: City?,
        repository: FakeSelectedCityRepository,
    ) = DetectNearestCityCommand(
        currentLocationProvider = object : CurrentLocationProvider {
            override suspend fun getCurrentLocation() = locationResult
        },
        cityResolver = object : CityResolver {
            override fun findNearestCity(
                coordinates: Coordinates,
            ) = resolvedCity

            override fun findCityById(cityId: String) =
                resolvedCity?.takeIf { city -> city.id == cityId }
        },
        selectedCityRepository = repository,
    )

    private class FakeSelectedCityRepository(
        private val saveError: Exception? = null,
    ) : SelectedCityRepository {
        private val selectedCity = MutableStateFlow<String?>(null)
        override val selectedCityId: Flow<String?> = selectedCity
        var savedCityId: String? = null
            private set

        override suspend fun setSelectedCity(cityId: String) {
            saveError?.let { throw it }
            savedCityId = cityId
            selectedCity.value = cityId
        }

        override suspend fun clearSelectedCity() {
            savedCityId = null
            selectedCity.value = null
        }
    }
}
