package com.example.newsagreggator.di

import android.content.Context
import com.example.newsagreggator.database.TokDatabase
import com.example.newsagreggator.database.dao.ArticleDao
import com.example.newsagreggator.database.dao.ArticleStateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideTokDatabase(
        @ApplicationContext context: Context,
    ): TokDatabase = TokDatabase.create(context)

    @Provides
    fun provideArticleDao(database: TokDatabase): ArticleDao =
        database.articleDao()

    @Provides
    fun provideArticleStateDao(database: TokDatabase): ArticleStateDao =
        database.articleStateDao()
}
