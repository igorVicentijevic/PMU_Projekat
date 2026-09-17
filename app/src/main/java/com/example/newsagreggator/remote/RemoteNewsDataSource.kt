package com.example.newsagreggator.remote

import com.example.newsagreggator.domain.model.Article

interface RemoteNewsDataSource {
    suspend fun fetchArticles(): List<Article>
}
