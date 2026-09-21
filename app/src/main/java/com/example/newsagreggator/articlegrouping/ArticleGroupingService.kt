package com.example.newsagreggator.articlegrouping

import com.example.newsagreggator.model.Article
import javax.inject.Inject

class ArticleGroupingService @Inject constructor(
    private val groupingStrategy: ArticleGroupingStrategy,
) {
    suspend fun getGroups(articles: List<Article>): List<ArticleGroup> =
        groupingStrategy.group(articles)
}
