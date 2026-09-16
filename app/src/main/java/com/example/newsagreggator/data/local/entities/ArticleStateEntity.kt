package com.example.newsagreggator.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_state")
data class ArticleStateEntity(
    @PrimaryKey val articleId: String,
    val isSaved: Boolean = false,
    val isRead: Boolean = false,
)
