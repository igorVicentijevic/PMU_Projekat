package com.example.newsagreggator.hilt

import com.example.newsagreggator.data.retrofit.datasource.RemoteNewsDataSource
import com.example.newsagreggator.data.retrofit.datasource.RssNewsDataSource
import com.example.newsagreggator.data.retrofit.newssource.NewsSource
import com.example.newsagreggator.data.retrofit.newssource.NewsSources
import com.example.newsagreggator.repository.NewsRepository
import com.example.newsagreggator.data.repository.RssNewsRepository
import com.example.newsagreggator.data.sample.createSampleNewsArticles
import com.example.newsagreggator.model.Article
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
