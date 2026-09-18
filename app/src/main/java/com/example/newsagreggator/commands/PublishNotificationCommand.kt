package com.example.newsagreggator.commands

import com.example.newsagreggator.notification.NotificationPublishResult
import com.example.newsagreggator.notification.NotificationPublisher
import com.example.newsagreggator.notification.AppNotification
import javax.inject.Inject

class PublishNotificationCommand @Inject constructor(
    private val notificationPublisher: NotificationPublisher,
) : Command<AppNotification, NotificationPublishResult>() {
    override suspend fun invoke(
        input: AppNotification,
    ): NotificationPublishResult = notificationPublisher.publish(input)
}
