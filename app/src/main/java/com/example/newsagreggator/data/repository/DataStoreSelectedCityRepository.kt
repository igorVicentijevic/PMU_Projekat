package com.example.newsagreggator.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.newsagreggator.data.preferences.userPreferencesDataStore
import com.example.newsagreggator.repository.SelectedCityRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreSelectedCityRepository @Inject constructor(
    @ApplicationContext context: Context,
) : SelectedCityRepository {
    private val dataStore = context.applicationContext.userPreferencesDataStore

    override val selectedCityId: Flow<String?> = dataStore.data.map { values ->
        values[SelectedCityId]
    }

    override suspend fun setSelectedCity(cityId: String) {
        dataStore.edit { values ->
            values[SelectedCityId] = cityId
        }
    }

    override suspend fun clearSelectedCity() {
        dataStore.edit { values ->
            values.remove(SelectedCityId)
        }
    }

    private companion object {
        val SelectedCityId = stringPreferencesKey("selected_city_id")
    }
}
