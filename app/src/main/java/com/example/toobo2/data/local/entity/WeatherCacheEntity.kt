package com.example.toobo2.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val locationId: Long,
    val temperature: Double,
    val apparentTemperature: Double,
    val humidity: Int,
    val weatherCode: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val precipitation: Double,
    val pressureMsl: Double,
    val uvIndex: Double,
    val isDay: Boolean,
    val dailyMaxTemp: Double,
    val dailyMinTemp: Double,
    val hourlyDataJson: String,
    val dailyDataJson: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
