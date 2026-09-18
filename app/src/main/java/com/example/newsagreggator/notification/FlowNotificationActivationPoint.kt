package com.example.newsagreggator.notification

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlowNotificationActivationPoint @Inject constructor() :
    NotificationActivationPoint {
    private val notifications = Channel<AppNotification>(Channel.BUFFERED)

    override fun notificationFlow(): Flow<AppNotification> =
        notifications.receiveAsFlow()

    override suspend fun activate(notification: AppNotification) {
        notifications.send(notification)
    }
}