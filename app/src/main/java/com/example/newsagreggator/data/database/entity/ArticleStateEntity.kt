package com.example.newsagreggator.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_state")
data class ArticleStateEntity(
    @PrimaryKey val articleId: String,
    val isSaved: Boolean = false,
    val isRead: Boolean = false,
)
