package com.example.newsagreggator.data.remote

import com.example.newsagreggator.business.model.Article

interface RemoteNewsDataSource {
    suspend fun fetchArticles(): List<Article>
}
