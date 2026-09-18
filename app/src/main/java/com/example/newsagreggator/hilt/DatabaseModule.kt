package com.example.newsagreggator.hilt

import android.content.Context
import com.example.newsagreggator.data.room.TokDatabase
import com.example.newsagreggator.data.room.dao.ArticleDao
import com.example.newsagreggator.data.room.dao.ArticleStateDao
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
