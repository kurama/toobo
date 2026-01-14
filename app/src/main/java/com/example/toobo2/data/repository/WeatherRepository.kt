package com.example.toobo2.data.repository

import com.example.toobo2.data.local.dao.WeatherCacheDao
import com.example.toobo2.data.mapper.toCacheEntity
import com.example.toobo2.data.mapper.toWeather
import com.example.toobo2.data.remote.api.WeatherApi
import com.example.toobo2.domain.model.Location
import com.example.toobo2.domain.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherCacheDao: WeatherCacheDao
) {

    companion object {
        private const val CACHE_VALIDITY_MS = 30 * 60 * 1000L // 30 minutes
    }

    fun getWeatherForLocationFlow(location: Location): Flow<Weather?> {
        return weatherCacheDao.getWeatherForLocation(location.id).map { cache ->
            cache?.toWeather(location)
        }
    }

    suspend fun fetchWeather(location: Location, forceRefresh: Boolean = false): Result<Weather> =
        withContext(Dispatchers.IO) {
            // Check cache first if not forcing refresh
            if (!forceRefresh) {
                val cachedWeather = weatherCacheDao.getWeatherForLocationOnce(location.id)
                if (cachedWeather != null && !isCacheExpired(cachedWeather.lastUpdated)) {
                    return@withContext Result.success(cachedWeather.toWeather(location))
                }
            }

            // Fetch from API
            try {
                val response = weatherApi.getWeather(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
                val weather = response.toWeather(location)

                // Cache the result
                weatherCacheDao.insertWeather(weather.toCacheEntity(location.id))

                Result.success(weather)
            } catch (e: Exception) {
                e.printStackTrace()
                // If API fails, try to return cached data even if expired
                val cachedWeather = weatherCacheDao.getWeatherForLocationOnce(location.id)
                if (cachedWeather != null) {
                    Result.success(cachedWeather.toWeather(location))
                } else {
                    Result.failure(e)
                }
            }
        }

    suspend fun clearOldCache() {
        val threshold = System.currentTimeMillis() - (24 * 60 * 60 * 1000L) // 24 hours
        weatherCacheDao.deleteOldCache(threshold)
    }

    private fun isCacheExpired(lastUpdated: Long): Boolean {
        return System.currentTimeMillis() - lastUpdated > CACHE_VALIDITY_MS
    }
}
