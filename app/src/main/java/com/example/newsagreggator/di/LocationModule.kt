package com.example.newsagreggator.di

import com.example.newsagreggator.business.repository.SelectedCityRepository
import com.example.newsagreggator.business.service.CityResolver
import com.example.newsagreggator.business.service.CurrentLocationProvider
import com.example.newsagreggator.data.location.AndroidGpsLocationProvider
import com.example.newsagreggator.data.location.SerbianCityResolver
import com.example.newsagreggator.data.preferences.DataStoreSelectedCityRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {
    @Binds
    @Singleton
    abstract fun bindCurrentLocationProvider(
        provider: AndroidGpsLocationProvider,
    ): CurrentLocationProvider

    @Binds
    @Singleton
    abstract fun bindCityResolver(
        resolver: SerbianCityResolver,
    ): CityResolver

    @Binds
    @Singleton
    abstract fun bindSelectedCityRepository(
        repository: DataStoreSelectedCityRepository,
    ): SelectedCityRepository
}
