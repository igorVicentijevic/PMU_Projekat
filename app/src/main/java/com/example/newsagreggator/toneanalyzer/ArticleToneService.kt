package com.example.newsagreggator.toneanalyzer

import com.example.newsagreggator.model.Article
import javax.inject.Inject

//entry-point for analyzing tone
class ArticleToneService @Inject constructor(
    private val analysisStrategy: ArticleToneAnalysisStrategy,
) {
    suspend fun getTone(article: Article): ArticleToneDistribution =
        analysisStrategy.analyze(article)
}
