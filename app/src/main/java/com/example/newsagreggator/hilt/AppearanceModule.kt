package com.example.newsagreggator.hilt

import com.example.newsagreggator.appearance.AmbientLightMonitor
import com.example.newsagreggator.appearance.AndroidAmbientLightMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppearanceModule {
    @Binds
    @Singleton
    abstract fun bindAmbientLightMonitor(
        monitor: AndroidAmbientLightMonitor,
    ): AmbientLightMonitor
}
