package com.example.newsagreggator.hilt

import com.example.newsagreggator.toneanalyzer.ArticleToneAnalysisStrategy
import com.example.newsagreggator.toneanalyzer.SampleArticleToneAnalysisStrategy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ToneAnalyzerModule {
    @Binds
    @Singleton
    abstract fun bindArticleToneAnalysisStrategy(
        strategy: SampleArticleToneAnalysisStrategy,
    ): ArticleToneAnalysisStrategy
}