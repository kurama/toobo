package com.example.toobo2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.toobo2.data.local.dao.LocationDao
import com.example.toobo2.data.local.dao.WeatherCacheDao
import com.example.toobo2.data.local.entity.SavedLocationEntity
import com.example.toobo2.data.local.entity.WeatherCacheEntity

@Database(
    entities = [SavedLocationEntity::class, WeatherCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun weatherCacheDao(): WeatherCacheDao

    companion object {
        const val DATABASE_NAME = "weather_database"
    }
}
