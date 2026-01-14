package com.example.toobo2.data.remote.api

import com.example.toobo2.data.remote.dto.NominatimResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ReverseGeocodingApi {

    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json",
        @Query("addressdetails") addressDetails: Int = 1,
        @Query("accept-language") language: String = "fr",
        @Header("User-Agent") userAgent: String = "WeatherApp/1.0"
    ): NominatimResponse

    companion object {
        const val BASE_URL = "https://nominatim.openstreetmap.org/"
    }
}
