package com.example.newsagreggator

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.newsagreggator.notification.BreakingNewsNotificationActivator
import com.example.newsagreggator.notification.NotificationActivationObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class TokApplication : Application(), Configuration.Provider {
    private val applicationScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationActivationObserver: NotificationActivationObserver

    @Inject
    lateinit var breakingNewsNotificationActivator:
        BreakingNewsNotificationActivator

    override fun onCreate() {
        super.onCreate()
        notificationActivationObserver.start(applicationScope)
        breakingNewsNotificationActivator.start(applicationScope)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
