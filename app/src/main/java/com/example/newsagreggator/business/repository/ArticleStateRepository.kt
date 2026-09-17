package com.example.newsagreggator.business.repository

import kotlinx.coroutines.flow.Flow

data class ArticleState(
    val savedArticleIds: Set<String> = emptySet(),
    val readArticleIds: Set<String> = emptySet(),
)

interface ArticleStateRepository {
    val articleState: Flow<ArticleState>

    suspend fun setArticleSaved(articleId: String, saved: Boolean)

    suspend fun markArticleRead(articleId: String)

    suspend fun clearReadingHistory()
}
