package com.example.newsagreggator

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.newsagreggator.notification.BreakingNewsNotificationActivator
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
    lateinit var breakingNewsNotificationActivator:
        BreakingNewsNotificationActivator

    override fun onCreate() {
        super.onCreate()
        breakingNewsNotificationActivator.start(applicationScope)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
