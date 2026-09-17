package com.example.newsagreggator.business.service

import com.example.newsagreggator.business.model.City
import com.example.newsagreggator.business.model.Coordinates

interface CityResolver {
    fun findNearestCity(coordinates: Coordinates): City?
}
