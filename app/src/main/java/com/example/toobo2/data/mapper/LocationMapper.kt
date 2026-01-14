package com.example.toobo2.data.mapper

import com.example.toobo2.data.local.entity.SavedLocationEntity
import com.example.toobo2.data.remote.dto.GeocodingResult
import com.example.toobo2.domain.model.Location

fun GeocodingResult.toLocation(): Location {
    return Location(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country,
        admin1 = admin1,
        isCurrentLocation = false
    )
}

fun SavedLocationEntity.toLocation(): Location {
    return Location(
        id = id,
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country,
        admin1 = admin1,
        isCurrentLocation = isCurrentLocation
    )
}

fun Location.toEntity(): SavedLocationEntity {
    return SavedLocationEntity(
        id = if (id > 0) id else 0,
        name = name,
        latitude = latitude,
        longitude = longitude,
        country = country,
        admin1 = admin1,
        isCurrentLocation = isCurrentLocation
    )
}
