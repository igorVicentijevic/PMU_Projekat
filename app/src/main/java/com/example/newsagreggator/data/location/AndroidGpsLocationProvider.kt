package com.example.newsagreggator.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.example.newsagreggator.business.model.Coordinates
import com.example.newsagreggator.business.service.CurrentLocationProvider
import com.example.newsagreggator.business.service.CurrentLocationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

class AndroidGpsLocationProvider @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : CurrentLocationProvider {
    private val locationManager =
        context.getSystemService(LocationManager::class.java)

    override suspend fun getCurrentLocation(): CurrentLocationResult {
        if (!hasLocationPermission()) {
            return CurrentLocationResult.PermissionRequired
        }
        if (
            !LocationManagerCompat.isLocationEnabled(locationManager) ||
            !isGpsProviderEnabled()
        ) {
            return CurrentLocationResult.LocationServicesDisabled
        }

        return try {
            val location = withTimeoutOrNull(LOCATION_TIMEOUT_MILLIS) {
                awaitGpsLocation()
            }
            if (location == null) {
                CurrentLocationResult.Unavailable(
                    IllegalStateException("GPS location request timed out")
                )
            } else {
                CurrentLocationResult.Available(
                    Coordinates(
                        latitude = location.latitude,
                        longitude = location.longitude,
                    )
                )
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: SecurityException) {
            CurrentLocationResult.PermissionRequired
        } catch (error: Exception) {
            CurrentLocationResult.Unavailable(error)
        }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED

    private fun isGpsProviderEnabled(): Boolean =
        runCatching {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }.getOrDefault(false)

    @SuppressLint("MissingPermission")
    private suspend fun awaitGpsLocation(): Location? =
        suspendCancellableCoroutine { continuation ->
            val cancellationSignal = CancellationSignal()
            continuation.invokeOnCancellation {
                cancellationSignal.cancel()
            }

            LocationManagerCompat.getCurrentLocation(
                locationManager,
                LocationManager.GPS_PROVIDER,
                cancellationSignal,
                ContextCompat.getMainExecutor(context),
            ) { location ->
                if (continuation.isActive) {
                    continuation.resume(location)
                }
            }
        }

    private companion object {
        const val LOCATION_TIMEOUT_MILLIS = 20_000L
    }
}
