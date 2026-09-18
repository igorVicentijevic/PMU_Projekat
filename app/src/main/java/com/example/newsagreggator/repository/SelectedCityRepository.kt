package com.example.newsagreggator.repository

import kotlinx.coroutines.flow.Flow

interface SelectedCityRepository {
    val selectedCityId: Flow<String?>

    suspend fun setSelectedCity(cityId: String)

    suspend fun clearSelectedCity()
}