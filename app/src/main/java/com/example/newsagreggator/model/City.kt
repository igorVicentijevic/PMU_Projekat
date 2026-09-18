package com.example.newsagreggator.model

data class Coordinates(
    val latitude: Double,
    val longitude: Double,
) {
    init {
        require(latitude in -90.0..90.0) {
            "Latitude must be between -90 and 90 degrees"
        }
        require(longitude in -180.0..180.0) {
            "Longitude must be between -180 and 180 degrees"
        }
    }
}

data class City(
    val id: String,
    val name: String,
    val coordinates: Coordinates,
    val aliases: Set<String> = emptySet(),
)
