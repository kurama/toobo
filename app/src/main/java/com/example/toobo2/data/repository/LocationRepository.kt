package com.example.toobo2.data.repository

import com.example.toobo2.data.local.dao.LocationDao
import com.example.toobo2.data.location.LocationService
import com.example.toobo2.data.mapper.toEntity
import com.example.toobo2.data.mapper.toLocation
import com.example.toobo2.data.remote.api.GeocodingApi
import com.example.toobo2.data.remote.api.ReverseGeocodingApi
import com.example.toobo2.domain.model.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val reverseGeocodingApi: ReverseGeocodingApi,
    private val locationDao: LocationDao,
    private val locationService: LocationService
) {

    fun getSavedLocations(): Flow<List<Location>> {
        return locationDao.getSavedLocations().map { entities ->
            entities.map { it.toLocation() }
        }
    }

    fun getCurrentLocationFlow(): Flow<Location?> {
        return locationDao.getCurrentLocation().map { it?.toLocation() }
    }

    suspend fun searchCities(query: String): Result<List<Location>> = withContext(Dispatchers.IO) {
        try {
            val response = geocodingApi.searchCity(query)
            val locations = response.results?.map { it.toLocation() } ?: emptyList()
            Result.success(locations)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun reverseGeocode(latitude: Double, longitude: Double): Location? = withContext(Dispatchers.IO) {
        try {
            val response = reverseGeocodingApi.reverseGeocode(latitude, longitude)
            val address = response.address

            if (address != null) {
                Location(
                    name = address.cityName ?: "Position actuelle",
                    latitude = latitude,
                    longitude = longitude,
                    country = address.country,
                    admin1 = address.state ?: address.region,
                    isCurrentLocation = true
                )
            } else {
                Location(
                    name = "Position actuelle",
                    latitude = latitude,
                    longitude = longitude,
                    isCurrentLocation = true
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Location(
                name = "Position actuelle",
                latitude = latitude,
                longitude = longitude,
                isCurrentLocation = true
            )
        }
    }

    suspend fun saveLocation(location: Location): Long {
        val locationToSave = location.copy(isCurrentLocation = false)
        return locationDao.insertLocation(locationToSave.toEntity())
    }

    suspend fun deleteLocation(location: Location) {
        locationDao.deleteLocation(location.toEntity())
    }

    suspend fun deleteLocationById(id: Long) {
        locationDao.deleteLocationById(id)
    }

    suspend fun isLocationSaved(latitude: Double, longitude: Double): Boolean {
        return locationDao.isLocationSaved(latitude, longitude)
    }

    suspend fun updateCurrentLocation(): Result<Location> = withContext(Dispatchers.IO) {
        if (!locationService.hasLocationPermission()) {
            return@withContext Result.failure(SecurityException("Location permission not granted"))
        }

        val androidLocation = locationService.getCurrentLocation()
            ?: locationService.getLastKnownLocation()

        if (androidLocation == null) {
            return@withContext Result.failure(Exception("Impossible de récupérer la position"))
        }

        val currentLocation = reverseGeocode(androidLocation.latitude, androidLocation.longitude)
            ?: Location(
                name = "Position actuelle",
                latitude = androidLocation.latitude,
                longitude = androidLocation.longitude,
                isCurrentLocation = true
            )

        locationDao.deleteCurrentLocation()
        val id = locationDao.insertLocation(currentLocation.toEntity())

        Result.success(currentLocation.copy(id = id))
    }

    fun hasLocationPermission(): Boolean = locationService.hasLocationPermission()
}
