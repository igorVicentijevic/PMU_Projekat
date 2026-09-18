package com.example.newsagreggator.data.notification

import com.example.newsagreggator.business.command.PublishNotificationCommand
import com.example.newsagreggator.business.service.NotificationActivationPoint
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Singleton
class NotificationActivationObserver @Inject constructor(
    private val notificationActivationPoint: NotificationActivationPoint,
    private val publishNotificationCommand: PublishNotificationCommand,
) {
    private var started = false

    @Synchronized
    fun start(scope: CoroutineScope) {
        if (started) {
            return
        }
        started = true

        scope.launch {
            notificationActivationPoint.notificationFlow()
                .collect { notification ->
                publishNotificationCommand(notification)
            }
        }
    }
}
