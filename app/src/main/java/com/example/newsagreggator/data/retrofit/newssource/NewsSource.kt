package com.example.newsagreggator.data.retrofit.newssource

import com.example.newsagreggator.model.NewsCategory

data class NewsSource(
    val id: String,
    val displayName: String,
    val feeds: List<NewsFeed>,
    val imageUrlNormalizer: (String) -> String = { it },
)

data class NewsFeed(
    val url: String,
    val category: NewsCategory? = null,
)
