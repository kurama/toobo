package com.example.toobo2.domain.model

data class Location(
    val id: Long = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null,
    val isCurrentLocation: Boolean = false
) {
    val displayName: String
        get() = buildString {
            append(name)
            admin1?.let { append(", $it") }
            country?.let { append(", $it") }
        }
}
