package com.example.newsagreggator.notification

fun interface NotificationPublisher {
    fun publish(notification: AppNotification): NotificationPublishResult
}

sealed interface NotificationPublishResult {
    data object Published : NotificationPublishResult

    data object PermissionRequired : NotificationPublishResult

    data object NotificationsDisabled : NotificationPublishResult

    data class Failed(
        val cause: Throwable,
    ) : NotificationPublishResult
}
