package com.example.newsagreggator.data.retrofit.datasource

import com.example.newsagreggator.model.Article

interface RemoteNewsDataSource {
    suspend fun fetchArticles(): List<Article>
}