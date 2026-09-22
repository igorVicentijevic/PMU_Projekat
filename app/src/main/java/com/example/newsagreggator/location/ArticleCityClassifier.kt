package com.example.newsagreggator.location

import com.example.newsagreggator.location.strategy.ArticleLocationDetectionStrategy
import com.example.newsagreggator.model.Article
import javax.inject.Inject

class ArticleCityClassifier @Inject constructor(
    private val strategy: ArticleLocationDetectionStrategy,
) {
    fun classify(article: Article): Set<String> =
        strategy.detectRelatedCityIds(article)
}