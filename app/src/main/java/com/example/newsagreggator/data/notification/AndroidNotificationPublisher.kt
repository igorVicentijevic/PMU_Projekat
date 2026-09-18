package com.example.newsagreggator.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.newsagreggator.MainActivity
import com.example.newsagreggator.R
import com.example.newsagreggator.business.model.AppNotification
import com.example.newsagreggator.business.service.NotificationPublishResult
import com.example.newsagreggator.business.service.NotificationPublisher
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidNotificationPublisher @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : NotificationPublisher {
    private val notificationManager = NotificationManagerCompat.from(context)

    override fun publish(
        notification: AppNotification,
    ): NotificationPublishResult {
        if (!hasNotificationPermission()) {
            return NotificationPublishResult.PermissionRequired
        }
        if (!notificationManager.areNotificationsEnabled()) {
            return NotificationPublishResult.NotificationsDisabled
        }

        return try {
            createChannel()
            notificationManager.notify(
                notification.id,
                NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(notification.title)
                    .setContentText(notification.message)
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(notification.message)
                    )
                    .setContentIntent(createContentIntent(notification))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build(),
            )
            NotificationPublishResult.Published
        } catch (error: RuntimeException) {
            NotificationPublishResult.Failed(error)
        }
    }

    private fun hasNotificationPermission(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(
                R.string.notification_channel_description
            )
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    private fun createContentIntent(
        notification: AppNotification,
    ): PendingIntent {
        val intent = notification.destinationUrl
            ?.let { destinationUrl ->
                Intent(Intent.ACTION_VIEW, Uri.parse(destinationUrl)).apply {
                    addCategory(Intent.CATEGORY_BROWSABLE)
                }
            }
            ?: Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        return PendingIntent.getActivity(
            context,
            notification.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val CHANNEL_ID = "general_notifications"
    }
}
