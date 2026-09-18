package com.example.newsagreggator.business.model

data class AppNotification(
    val id: Int,
    val title: String,
    val message: String,
    val destinationUrl: String? = null,
)
