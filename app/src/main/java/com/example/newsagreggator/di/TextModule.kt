package com.example.newsagreggator.di

import com.example.newsagreggator.business.service.TextNormalizer
import com.example.newsagreggator.data.text.SerbianTextNormalizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TextModule {
    @Binds
    @Singleton
    abstract fun bindTextNormalizer(
        normalizer: SerbianTextNormalizer,
    ): TextNormalizer
}
