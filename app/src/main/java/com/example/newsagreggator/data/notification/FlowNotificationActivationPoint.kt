package com.example.newsagreggator.data.notification

import com.example.newsagreggator.business.model.AppNotification
import com.example.newsagreggator.business.service.NotificationActivationPoint
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

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
