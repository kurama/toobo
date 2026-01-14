package com.example.toobo2.data.remote.dto

import com.google.gson.annotations.SerializedName

data class NominatimResponse(
    @SerializedName("place_id")
    val placeId: Long? = null,
    val licence: String? = null,
    @SerializedName("osm_type")
    val osmType: String? = null,
    @SerializedName("osm_id")
    val osmId: Long? = null,
    val lat: String? = null,
    val lon: String? = null,
    @SerializedName("display_name")
    val displayName: String? = null,
    val address: NominatimAddress? = null,
    val boundingbox: List<String>? = null
)

data class NominatimAddress(
    val village: String? = null,
    val town: String? = null,
    val city: String? = null,
    val municipality: String? = null,
    val county: String? = null,
    val state: String? = null,
    @SerializedName("ISO3166-2-lvl4")
    val stateCode: String? = null,
    val region: String? = null,
    val postcode: String? = null,
    val country: String? = null,
    @SerializedName("country_code")
    val countryCode: String? = null
) {
    val cityName: String?
        get() = city ?: town ?: village ?: municipality ?: county
}
