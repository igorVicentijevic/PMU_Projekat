package com.example.newsagreggator.articlegrouping

import com.example.newsagreggator.model.Article

data class ArticleGroup(
    val id: String,
    val title: String,
    val articles: List<Article>,
)
