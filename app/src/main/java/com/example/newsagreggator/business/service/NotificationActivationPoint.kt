package com.example.newsagreggator.business.service

import com.example.newsagreggator.business.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationActivationPoint {
    fun notificationFlow(): Flow<AppNotification>

    suspend fun activate(notification: AppNotification)
}
