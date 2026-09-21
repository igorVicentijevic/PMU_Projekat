package com.example.newsagreggator.hilt

import com.example.newsagreggator.articlegrouping.ArticleGroupingStrategy
import com.example.newsagreggator.articlegrouping.MockArticleGroupingStrategy
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ArticleGroupingModule {
    @Binds
    @Singleton
    abstract fun bindArticleGroupingStrategy(
        strategy: MockArticleGroupingStrategy,
    ): ArticleGroupingStrategy
}
