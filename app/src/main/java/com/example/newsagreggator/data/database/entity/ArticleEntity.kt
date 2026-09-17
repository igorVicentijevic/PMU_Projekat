package com.example.newsagreggator.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsagreggator.business.model.Article
import com.example.newsagreggator.business.model.NewsCategory

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val source: String,
    val category: String,
    val publishedAtEpochMillis: Long,
    val imageUrl: String?,
    val articleUrl: String,
    val relatedCityIds: String,
)

fun Article.toEntity(): ArticleEntity = ArticleEntity(
    id = id,
    title = title,
    summary = summary,
    source = source,
    category = category.name,
    publishedAtEpochMillis = publishedAtEpochMillis,
    imageUrl = imageUrl,
    articleUrl = articleUrl,
    relatedCityIds = relatedCityIds.sorted().joinToString(","),
)

fun ArticleEntity.toDomain(): Article? {
    val newsCategory = NewsCategory.entries.firstOrNull {
        it.name == category
    } ?: return null
    return Article(
        id = id,
        title = title,
        summary = summary,
        source = source,
        category = newsCategory,
        publishedAtEpochMillis = publishedAtEpochMillis,
        imageUrl = imageUrl,
        articleUrl = articleUrl,
        relatedCityIds = relatedCityIds
            .split(',')
            .filter(String::isNotBlank)
            .toSet(),
    )
}
