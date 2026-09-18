package com.example.newsagreggator.notification

import kotlinx.coroutines.flow.Flow

interface NotificationActivationPoint {
    fun notificationFlow(): Flow<AppNotification>

    suspend fun activate(notification: AppNotification)
}