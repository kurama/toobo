package com.example.toobo2.data.remote.api

import com.example.toobo2.data.remote.dto.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {

    @GET("v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "fr",
        @Query("format") format: String = "json"
    ): GeocodingResponse

    companion object {
        const val BASE_URL = "https://geocoding-api.open-meteo.com/"
    }
}
