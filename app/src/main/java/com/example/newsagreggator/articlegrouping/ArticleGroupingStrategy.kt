package com.example.newsagreggator.articlegrouping

import com.example.newsagreggator.model.Article

interface ArticleGroupingStrategy {
    suspend fun group(articles: List<Article>): List<ArticleGroup>
}
