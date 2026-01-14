package com.example.toobo2.data.remote.api

import com.example.toobo2.data.remote.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = CURRENT_PARAMS,
        @Query("hourly") hourly: String = HOURLY_PARAMS,
        @Query("daily") daily: String = DAILY_PARAMS,
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7
    ): WeatherResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"

        private const val CURRENT_PARAMS = "temperature_2m,relative_humidity_2m,apparent_temperature," +
                "is_day,precipitation,rain,showers,snowfall,weather_code,cloud_cover," +
                "pressure_msl,surface_pressure,wind_speed_10m,wind_direction_10m,wind_gusts_10m,uv_index"

        private const val HOURLY_PARAMS = "temperature_2m,relative_humidity_2m,apparent_temperature," +
                "precipitation,weather_code,wind_speed_10m,is_day"

        private const val DAILY_PARAMS = "weather_code,temperature_2m_max,temperature_2m_min," +
                "apparent_temperature_max,apparent_temperature_min,sunrise,sunset,uv_index_max," +
                "precipitation_sum,precipitation_probability_max,wind_speed_10m_max"
    }
}
