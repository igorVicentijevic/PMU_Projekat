package com.example.newsagreggator.data

import com.example.newsagreggator.domain.model.Article

interface NewsRepository {
    fun getArticles(): List<Article>

    suspend fun refreshArticles(): Result<List<Article>>
}
