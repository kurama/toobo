package com.example.toobo2.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    companion object {
        private const val LOCATION_TIMEOUT_MS = 15000L
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            withTimeout(LOCATION_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    val cancellationToken = CancellationTokenSource()

                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                        cancellationToken.token
                    ).addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }.addOnFailureListener { _ ->
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }.addOnCanceledListener {
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }

                    continuation.invokeOnCancellation {
                        cancellationToken.cancel()
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            null
        } catch (e: SecurityException) {
            null
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            withTimeout(LOCATION_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (continuation.isActive) {
                                continuation.resume(location)
                            }
                        }
                        .addOnFailureListener { _ ->
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                        .addOnCanceledListener {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                }
            }
        } catch (e: TimeoutCancellationException) {
            null
        } catch (e: SecurityException) {
            null
        }
    }
}
