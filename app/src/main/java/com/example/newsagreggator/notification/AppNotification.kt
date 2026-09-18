package com.example.newsagreggator.notification

data class AppNotification(
    val id: Int,
    val title: String,
    val message: String,
    val destinationUrl: String? = null,
)