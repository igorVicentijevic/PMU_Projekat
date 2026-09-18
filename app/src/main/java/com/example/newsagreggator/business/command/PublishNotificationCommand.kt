package com.example.newsagreggator.business.command

import com.example.newsagreggator.business.model.AppNotification
import com.example.newsagreggator.business.service.NotificationPublishResult
import com.example.newsagreggator.business.service.NotificationPublisher
import javax.inject.Inject

class PublishNotificationCommand @Inject constructor(
    private val notificationPublisher: NotificationPublisher,
) : Command<AppNotification, NotificationPublishResult>() {
    override suspend fun invoke(
        input: AppNotification,
    ): NotificationPublishResult = notificationPublisher.publish(input)
}
