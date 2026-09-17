package com.example.newsagreggator.di

import com.example.newsagreggator.business.service.NewsRefreshScheduler
import com.example.newsagreggator.data.background.WorkManagerNewsRefreshScheduler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackgroundModule {
    @Binds
    @Singleton
    abstract fun bindNewsRefreshScheduler(
        scheduler: WorkManagerNewsRefreshScheduler,
    ): NewsRefreshScheduler
}
