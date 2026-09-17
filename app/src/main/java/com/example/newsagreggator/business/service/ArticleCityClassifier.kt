package com.example.newsagreggator.business.service

import com.example.newsagreggator.business.model.Article
import javax.inject.Inject

class ArticleCityClassifier @Inject constructor(
    private val strategy: ArticleLocationDetectionStrategy,
) {
    fun classify(article: Article): Set<String> =
        strategy.detectRelatedCityIds(article)
}
