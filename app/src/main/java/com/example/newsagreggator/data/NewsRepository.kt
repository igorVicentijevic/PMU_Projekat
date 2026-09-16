package com.example.newsagreggator.data

import com.example.newsagreggator.domain.model.Article
import kotlinx.coroutines.flow.Flow

data class NewsSnapshot(
    val articles: List<Article>,
    val lastSuccessfulRefreshEpochMillis: Long?,
)

interface NewsRepository {
    val news: Flow<NewsSnapshot>

    suspend fun refreshArticles(): Result<Unit>
}
