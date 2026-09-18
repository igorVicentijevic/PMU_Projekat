package com.example.newsagreggator.hilt

import com.example.newsagreggator.util.TextNormalizer
import com.example.newsagreggator.util.SerbianTextNormalizer
import com.example.newsagreggator.util.comparator.LevenshteinWordComparator
import com.example.newsagreggator.util.comparator.WordComparator
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

    @Binds
    @Singleton
    abstract fun bindWordComparator(
        comparator: LevenshteinWordComparator,
    ): WordComparator
}
