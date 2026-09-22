package com.example.newsagreggator.hilt

import com.example.newsagreggator.notification.AndroidNotificationPublisher
import com.example.newsagreggator.notification.NotificationPublisher
import com.example.newsagreggator.notification.strategy.BreakingNewsStrategy
import com.example.newsagreggator.notification.strategy.KeywordBreakingNewsStrategy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    @Singleton
    abstract fun bindNotificationPublisher(
        publisher: AndroidNotificationPublisher,
    ): NotificationPublisher

    @Binds
    @Singleton
    abstract fun bindBreakingNewsStrategy(
        strategy: KeywordBreakingNewsStrategy,
    ): BreakingNewsStrategy
}
