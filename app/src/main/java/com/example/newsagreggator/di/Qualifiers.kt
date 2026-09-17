package com.example.newsagreggator.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class InitialArticles

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NewsSourceCatalog
