package com.example.newsagreggator.ui.stateholders

data class ArticleGroupUiModel(
    val id: String,
    val title: String,
    val articles: List<NewsCardUiModel>,
)
