package com.example.toobo2.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponse(
    val results: List<GeocodingResult>? = null,
    @SerializedName("generationtime_ms")
    val generationTimeMs: Double? = null
)

data class GeocodingResult(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevation: Double? = null,
    @SerializedName("feature_code")
    val featureCode: String? = null,
    @SerializedName("country_code")
    val countryCode: String? = null,
    val country: String? = null,
    val admin1: String? = null,
    val admin2: String? = null,
    val admin3: String? = null,
    val admin4: String? = null,
    val timezone: String? = null,
    val population: Int? = null
)
