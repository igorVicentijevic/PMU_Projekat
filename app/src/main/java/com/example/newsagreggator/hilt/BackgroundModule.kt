package com.example.newsagreggator.hilt

import com.example.newsagreggator.background.NewsRefreshScheduler
import com.example.newsagreggator.background.WorkManagerNewsRefreshScheduler
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
