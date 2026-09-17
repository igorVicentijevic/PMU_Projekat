package com.example.newsagreggator.di

import com.example.newsagreggator.remote.api.RssNewsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRssNewsService(): RssNewsService =
        Retrofit.Builder()
            .baseUrl("https://www.rts.rs/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create()
}
