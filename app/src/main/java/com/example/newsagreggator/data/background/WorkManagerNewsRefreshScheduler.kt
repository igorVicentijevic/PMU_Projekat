package com.example.newsagreggator.data.background

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.newsagreggator.business.service.NewsRefreshScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerNewsRefreshScheduler @Inject constructor(
    @ApplicationContext context: Context,
) : NewsRefreshScheduler {
    private val workManager = WorkManager.getInstance(context)

    override fun schedule(intervalMinutes: Int) {
        val repeatInterval = intervalMinutes.coerceAtLeast(
            NewsRefreshScheduler.MIN_INTERVAL_MINUTES
        )
        val refreshRequest =
            PeriodicWorkRequestBuilder<NewsRefreshWorker>(
                repeatInterval.toLong(),
                TimeUnit.MINUTES,
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()

        workManager.enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            refreshRequest,
        )
    }

    private companion object {
        const val UNIQUE_WORK_NAME = "periodic-news-refresh"
    }
}
