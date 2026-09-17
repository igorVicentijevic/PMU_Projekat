package com.example.newsagreggator.di

import com.example.newsagreggator.business.repository.ArticleStateRepository
import com.example.newsagreggator.business.repository.UserPreferencesRepository
import com.example.newsagreggator.business.service.NetworkMonitor
import com.example.newsagreggator.data.network.AndroidNetworkMonitor
import com.example.newsagreggator.data.preferences.DataStoreUserPreferencesRepository
import com.example.newsagreggator.data.repository.RoomArticleStateRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindArticleStateRepository(
        repository: RoomArticleStateRepository,
    ): ArticleStateRepository

    @Binds
    @Singleton
    abstract fun bindNetworkMonitor(
        monitor: AndroidNetworkMonitor,
    ): NetworkMonitor

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        repository: DataStoreUserPreferencesRepository,
    ): UserPreferencesRepository
}
