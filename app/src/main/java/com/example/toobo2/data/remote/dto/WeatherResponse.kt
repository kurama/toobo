package com.example.toobo2.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    @SerialName("generationtime_ms")
    val generationTimeMs: Double,
    @SerialName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    val timezone: String,
    @SerialName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    val elevation: Double,
    @SerialName("current")
    val current: CurrentWeather? = null,
    @SerialName("hourly")
    val hourly: HourlyWeather? = null,
    @SerialName("daily")
    val daily: DailyWeather? = null
)

@Serializable
data class CurrentWeather(
    val time: String,
    val interval: Int,
    @SerialName("temperature_2m")
    val temperature: Double,
    @SerialName("relative_humidity_2m")
    val relativeHumidity: Int,
    @SerialName("apparent_temperature")
    val apparentTemperature: Double,
    @SerialName("is_day")
    val isDay: Int,
    val precipitation: Double,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    @SerialName("weather_code")
    val weatherCode: Int,
    @SerialName("cloud_cover")
    val cloudCover: Int,
    @SerialName("pressure_msl")
    val pressureMsl: Double,
    @SerialName("surface_pressure")
    val surfacePressure: Double,
    @SerialName("wind_speed_10m")
    val windSpeed: Double,
    @SerialName("wind_direction_10m")
    val windDirection: Int,
    @SerialName("wind_gusts_10m")
    val windGusts: Double,
    @SerialName("uv_index")
    val uvIndex: Double? = null
)

@Serializable
data class HourlyWeather(
    val time: List<String>,
    @SerialName("temperature_2m")
    val temperature: List<Double>,
    @SerialName("relative_humidity_2m")
    val relativeHumidity: List<Int>,
    @SerialName("apparent_temperature")
    val apparentTemperature: List<Double>,
    val precipitation: List<Double>,
    @SerialName("weather_code")
    val weatherCode: List<Int>,
    @SerialName("wind_speed_10m")
    val windSpeed: List<Double>,
    @SerialName("is_day")
    val isDay: List<Int>
)

@Serializable
data class DailyWeather(
    val time: List<String>,
    @SerialName("weather_code")
    val weatherCode: List<Int>,
    @SerialName("temperature_2m_max")
    val temperatureMax: List<Double>,
    @SerialName("temperature_2m_min")
    val temperatureMin: List<Double>,
    @SerialName("apparent_temperature_max")
    val apparentTemperatureMax: List<Double>,
    @SerialName("apparent_temperature_min")
    val apparentTemperatureMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerialName("uv_index_max")
    val uvIndexMax: List<Double>,
    @SerialName("precipitation_sum")
    val precipitationSum: List<Double>,
    @SerialName("precipitation_probability_max")
    val precipitationProbabilityMax: List<Int>? = null,
    @SerialName("wind_speed_10m_max")
    val windSpeedMax: List<Double>
)
