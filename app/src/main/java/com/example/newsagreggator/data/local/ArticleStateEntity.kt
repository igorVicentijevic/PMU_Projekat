package com.example.newsagreggator.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_state")
data class ArticleStateEntity(
    @PrimaryKey val articleId: Int,
    val isSaved: Boolean = false,
    val isRead: Boolean = false,
)
