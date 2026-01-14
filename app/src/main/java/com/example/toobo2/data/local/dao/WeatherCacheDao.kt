package com.example.toobo2.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.toobo2.data.local.entity.WeatherCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherCacheDao {

    @Query("SELECT * FROM weather_cache WHERE locationId = :locationId")
    fun getWeatherForLocation(locationId: Long): Flow<WeatherCacheEntity?>

    @Query("SELECT * FROM weather_cache WHERE locationId = :locationId")
    suspend fun getWeatherForLocationOnce(locationId: Long): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherCacheEntity)

    @Query("DELETE FROM weather_cache WHERE locationId = :locationId")
    suspend fun deleteWeatherForLocation(locationId: Long)

    @Query("DELETE FROM weather_cache WHERE lastUpdated < :timestamp")
    suspend fun deleteOldCache(timestamp: Long)

    @Query("DELETE FROM weather_cache")
    suspend fun clearAllCache()
}
