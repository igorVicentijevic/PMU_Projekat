package com.example.newsagreggator.business.service

import com.example.newsagreggator.business.model.Article

fun interface ArticleLocationDetectionStrategy {
    fun detectRelatedCityIds(article: Article): Set<String>
}
