package com.example.newsagreggator.di

import com.example.newsagreggator.business.service.NotificationPublisher
import com.example.newsagreggator.data.notification.AndroidNotificationPublisher
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
}
