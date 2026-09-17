package com.example.newsagreggator.business.model

enum class NewsCategory {
    Serbia,
    World,
    Technology,
    Business,
    Culture,
    Sport,
    Health,
}

data class Article(
    val id: String,
    val title: String,
    val summary: String,
    val source: String,
    val category: NewsCategory,
    val publishedAtEpochMillis: Long,
    val imageUrl: String?,
    val articleUrl: String,
    val relatedCityIds: Set<String> = emptySet(),
)
