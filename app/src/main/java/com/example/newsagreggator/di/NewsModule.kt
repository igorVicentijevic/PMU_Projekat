package com.example.newsagreggator.di

import com.example.newsagreggator.remote.RemoteNewsDataSource
import com.example.newsagreggator.remote.rss.RssNewsDataSource
import com.example.newsagreggator.remote.source.NewsSource
import com.example.newsagreggator.remote.source.NewsSources
import com.example.newsagreggator.repository.NewsRepository
import com.example.newsagreggator.repository.RssNewsRepository
import com.example.newsagreggator.sample.createSampleNewsArticles
import com.example.newsagreggator.domain.model.Article
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsBindingsModule {
    @Binds
    @Singleton
    abstract fun bindRemoteNewsDataSource(
        dataSource: RssNewsDataSource,
    ): RemoteNewsDataSource

    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        repository: RssNewsRepository,
    ): NewsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NewsConfigurationModule {
    @Provides
    @NewsSourceCatalog
    fun provideNewsSources(): List<NewsSource> = NewsSources.all

    @Provides
    @InitialArticles
    fun provideInitialArticles(): List<Article> = createSampleNewsArticles()
}
