package com.example.toobo2.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("generationtime_ms")
    val generationTimeMs: Double,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    val elevation: Double,
    @SerializedName("current")
    val current: CurrentWeather? = null,
    @SerializedName("hourly")
    val hourly: HourlyWeather? = null,
    @SerializedName("daily")
    val daily: DailyWeather? = null
)

data class CurrentWeather(
    val time: String,
    val interval: Int,
    @SerializedName("temperature_2m")
    val temperature: Double,
    @SerializedName("relative_humidity_2m")
    val relativeHumidity: Int,
    @SerializedName("apparent_temperature")
    val apparentTemperature: Double,
    @SerializedName("is_day")
    val isDay: Int,
    val precipitation: Double,
    val rain: Double,
    val showers: Double,
    val snowfall: Double,
    @SerializedName("weather_code")
    val weatherCode: Int,
    @SerializedName("cloud_cover")
    val cloudCover: Int,
    @SerializedName("pressure_msl")
    val pressureMsl: Double,
    @SerializedName("surface_pressure")
    val surfacePressure: Double,
    @SerializedName("wind_speed_10m")
    val windSpeed: Double,
    @SerializedName("wind_direction_10m")
    val windDirection: Int,
    @SerializedName("wind_gusts_10m")
    val windGusts: Double,
    @SerializedName("uv_index")
    val uvIndex: Double? = null
)

data class HourlyWeather(
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperature: List<Double>,
    @SerializedName("relative_humidity_2m")
    val relativeHumidity: List<Int>,
    @SerializedName("apparent_temperature")
    val apparentTemperature: List<Double>,
    val precipitation: List<Double>,
    @SerializedName("weather_code")
    val weatherCode: List<Int>,
    @SerializedName("wind_speed_10m")
    val windSpeed: List<Double>,
    @SerializedName("is_day")
    val isDay: List<Int>
)

data class DailyWeather(
    val time: List<String>,
    @SerializedName("weather_code")
    val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max")
    val temperatureMax: List<Double>,
    @SerializedName("temperature_2m_min")
    val temperatureMin: List<Double>,
    @SerializedName("apparent_temperature_max")
    val apparentTemperatureMax: List<Double>,
    @SerializedName("apparent_temperature_min")
    val apparentTemperatureMin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerializedName("uv_index_max")
    val uvIndexMax: List<Double>,
    @SerializedName("precipitation_sum")
    val precipitationSum: List<Double>,
    @SerializedName("precipitation_probability_max")
    val precipitationProbabilityMax: List<Int>? = null,
    @SerializedName("wind_speed_10m_max")
    val windSpeedMax: List<Double>
)
