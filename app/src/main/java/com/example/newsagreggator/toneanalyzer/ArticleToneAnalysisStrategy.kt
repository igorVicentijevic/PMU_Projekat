package com.example.newsagreggator.toneanalyzer

import com.example.newsagreggator.model.Article

interface ArticleToneAnalysisStrategy {
    suspend fun analyze(article: Article): ArticleToneDistribution
}
