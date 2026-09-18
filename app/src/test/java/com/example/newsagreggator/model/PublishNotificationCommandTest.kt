package com.example.newsagreggator.model

import com.example.newsagreggator.commands.PublishNotificationCommand
import com.example.newsagreggator.notification.AppNotification
import com.example.newsagreggator.notification.NotificationPublishResult
import com.example.newsagreggator.notification.NotificationPublisher
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class PublishNotificationCommandTest {
    @Test
    fun forwardsNotificationDataToPublisher() = runBlocking {
        val notification = AppNotification(
            id = 42,
            title = "Naslov",
            message = "Sadržaj obaveštenja",
            destinationUrl = "https://example.com/article",
        )
        var publishedNotification: AppNotification? = null
        val expectedResult = NotificationPublishResult.Published
        val command = PublishNotificationCommand(
            notificationPublisher = NotificationPublisher { input ->
                publishedNotification = input
                expectedResult
            }
        )

        val result = command(notification)

        assertEquals(notification, publishedNotification)
        assertSame(expectedResult, result)
    }
}
