package com.example.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface LocationTracker {
    fun hasLocationPermission(): Boolean
    fun getLocationUpdates(intervalMs: Long = 3000L): Flow<UserLocation>
    suspend fun getCurrentLocation(): UserLocation?
}

class DefaultLocationTracker(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) : LocationTracker {

    override fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocationGranted || coarseLocationGranted
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): UserLocation? {
        if (!hasLocationPermission()) return null

        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()

            location?.toUserLocation() ?: getFallbackLocation()
        } catch (e: Exception) {
            getFallbackLocation()
        }
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMs: Long): Flow<UserLocation> = callbackFlow {
        if (!hasLocationPermission()) {
            close()
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs / 2)
            .setMinUpdateDistanceMeters(1f) // Notificar a cada 1 metro de deslocamento
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location.toUserLocation())
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            // Fallback para LocationManager caso o Google Play Services falhe
            startLocationManagerUpdates(intervalMs) { loc ->
                trySend(loc)
            }
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getFallbackLocation(): UserLocation? {
        if (!hasLocationPermission()) return null
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return null
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val loc = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || loc.accuracy < bestLocation.accuracy) {
                bestLocation = loc
            }
        }
        return bestLocation?.toUserLocation()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationManagerUpdates(
        intervalMs: Long,
        onLocationChanged: (UserLocation) -> Unit
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                onLocationChanged(location.toUserLocation())
            }
        }

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    intervalMs,
                    1f,
                    listener,
                    Looper.getMainLooper()
                )
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    intervalMs,
                    1f,
                    listener,
                    Looper.getMainLooper()
                )
            }
        } catch (_: Exception) {
        }
    }

    private fun Location.toUserLocation(): UserLocation {
        val isMockLocation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isMock
        } else {
            @Suppress("DEPRECATION")
            isFromMockProvider
        }

        return UserLocation(
            latitude = latitude,
            longitude = longitude,
            accuracyMeters = if (hasAccuracy()) accuracy else null,
            bearing = if (hasBearing() && bearing != 0f) bearing else null,
            speedMps = if (hasSpeed()) speed else null,
            altitudeMeters = if (hasAltitude()) altitude else null,
            timestamp = time,
            provider = provider ?: "fused",
            isMock = isMockLocation
        )
    }
}

object LocationUtils {
    /**
     * Calcula a distância geodésica física em metros entre duas coordenadas.
     */
    fun calculateDistanceMeters(
        startLat: Double, startLon: Double,
        endLat: Double, endLon: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(startLat, startLon, endLat, endLon, results)
        return results[0]
    }

    /**
     * Calcula a distância em quilômetros com arredondamento para 1 casa decimal.
     */
    fun calculateDistanceKm(
        startLat: Double, startLon: Double,
        endLat: Double, endLon: Double
    ): Double {
        val meters = calculateDistanceMeters(startLat, startLon, endLat, endLon)
        return Math.round((meters / 1000.0) * 10.0) / 10.0
    }
}
