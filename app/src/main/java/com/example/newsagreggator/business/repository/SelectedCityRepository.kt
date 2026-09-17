package com.example.newsagreggator.business.repository

import kotlinx.coroutines.flow.Flow

interface SelectedCityRepository {
    val selectedCityId: Flow<String?>

    suspend fun setSelectedCity(cityId: String)

    suspend fun clearSelectedCity()
}
