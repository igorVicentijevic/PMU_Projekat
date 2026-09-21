package com.example.newsagreggator.pdf.data

data class ArticlePdfContent(
    val title: String,
    val summary: String,
    val source: String,
    val category: String,
    val publishedTime: String,
    val originalUrl: String,
    val exportedAt: String,
    val labels: ArticlePdfLabels,
)

data class ArticlePdfLabels(
    val appName: String,
    val originalArticle: String,
    val exportedAt: String,
    val savedWithApp: String,
    val page: String,
    val summaryUnavailable: String,
)
