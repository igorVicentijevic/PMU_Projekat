package com.example.newsagreggator.hilt

import com.example.newsagreggator.pdf.exporter.AndroidArticlePdfExporter
import com.example.newsagreggator.pdf.exporter.ArticlePdfExporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PdfModule {
    @Binds
    @Singleton
    abstract fun bindArticlePdfExporter(
        exporter: AndroidArticlePdfExporter,
    ): ArticlePdfExporter
}