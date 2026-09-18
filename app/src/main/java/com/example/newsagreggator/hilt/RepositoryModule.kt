package com.example.newsagreggator.hilt

import com.example.newsagreggator.repository.ArticleStateRepository
import com.example.newsagreggator.repository.UserPreferencesRepository
import com.example.newsagreggator.data.repository.DataStoreUserPreferencesRepository
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
    abstract fun bindUserPreferencesRepository(
        repository: DataStoreUserPreferencesRepository,
    ): UserPreferencesRepository
}
