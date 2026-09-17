package com.example.newsagreggator.di

import com.example.newsagreggator.repository.ArticleStateRepository
import com.example.newsagreggator.repository.RoomArticleStateRepository
import com.example.newsagreggator.network.AndroidNetworkMonitor
import com.example.newsagreggator.network.NetworkMonitor
import com.example.newsagreggator.preferences.DataStoreUserPreferencesRepository
import com.example.newsagreggator.preferences.UserPreferencesRepository
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
