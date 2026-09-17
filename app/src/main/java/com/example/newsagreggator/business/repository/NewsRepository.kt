package com.example.newsagreggator.business.repository

import com.example.newsagreggator.business.model.Article
import kotlinx.coroutines.flow.Flow

data class NewsSnapshot(
    val articles: List<Article>,
    val lastSuccessfulRefreshEpochMillis: Long?,
)

interface NewsRepository {
    val news: Flow<NewsSnapshot>

    suspend fun refreshArticles(): Result<Unit>

    suspend fun refreshArticlesIfStale(
        staleAfterMillis: Long,
    ): Result<Unit> = refreshArticles()
}
