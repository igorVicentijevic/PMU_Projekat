package com.example.newsagreggator.location

import com.example.newsagreggator.model.City
import com.example.newsagreggator.model.Coordinates

interface CityResolver {
    fun findNearestCity(coordinates: Coordinates): City?

    fun findCityById(cityId: String): City?
}