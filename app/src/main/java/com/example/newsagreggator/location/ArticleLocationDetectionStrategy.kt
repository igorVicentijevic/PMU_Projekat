package com.example.newsagreggator.location

import com.example.newsagreggator.model.Article

fun interface ArticleLocationDetectionStrategy {
    fun detectRelatedCityIds(article: Article): Set<String>
}