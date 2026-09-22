package com.example.newsagreggator.hilt

import com.example.newsagreggator.repository.SelectedCityRepository
import com.example.newsagreggator.location.ArticleLocationDetectionStrategy
import com.example.newsagreggator.location.CityCatalog
import com.example.newsagreggator.location.CityResolver
import com.example.newsagreggator.location.CurrentLocationProvider
import com.example.newsagreggator.location.AliasArticleLocationDetectionStrategy
import com.example.newsagreggator.location.AndroidGpsLocationProvider
import com.example.newsagreggator.location.SerbianCityResolver
import com.example.newsagreggator.repository.DataStoreSelectedCityRepository
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
    abstract fun bindCityCatalog(
        resolver: SerbianCityResolver,
    ): CityCatalog

    @Binds
    @Singleton
    abstract fun bindArticleLocationDetectionStrategy(
        strategy: AliasArticleLocationDetectionStrategy,
    ): ArticleLocationDetectionStrategy

    @Binds
    @Singleton
    abstract fun bindSelectedCityRepository(
        repository: DataStoreSelectedCityRepository,
    ): SelectedCityRepository
}
