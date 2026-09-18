package com.example.newsagreggator.notification

import com.example.newsagreggator.commands.PublishNotificationCommand
import com.example.newsagreggator.notification.AppNotification
import com.example.newsagreggator.notification.NotificationActivationPoint
import com.example.newsagreggator.notification.NotificationPublishResult
import com.example.newsagreggator.notification.NotificationPublisher
import com.example.newsagreggator.notification.NotificationActivationObserver
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationActivationObserverTest {
    @Test
    fun forwardsStrategyActivationToNotificationCommand() = runBlocking {
        val notifications = MutableSharedFlow<AppNotification>()
        val activationPoint = object : NotificationActivationPoint {
            override fun notificationFlow() = notifications

            override suspend fun activate(notification: AppNotification) {
                notifications.emit(notification)
            }
        }
        val publishedNotification = CompletableDeferred<AppNotification>()
        val observer = NotificationActivationObserver(
            notificationActivationPoint = activationPoint,
            publishNotificationCommand = PublishNotificationCommand(
                NotificationPublisher { notification ->
                    publishedNotification.complete(notification)
                    NotificationPublishResult.Published
                }
            ),
        )
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val notification = AppNotification(
            id = 7,
            title = "Naslov",
            message = "Poruka",
        )

        observer.start(scope)
        activationPoint.activate(notification)

        assertEquals(
            notification,
            withTimeout(1_000L) { publishedNotification.await() },
        )
        scope.cancel()
    }
}
