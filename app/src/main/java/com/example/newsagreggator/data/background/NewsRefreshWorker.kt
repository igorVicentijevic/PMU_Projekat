package com.example.newsagreggator.data.background

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.newsagreggator.business.repository.NewsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NewsRefreshWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val newsRepository: NewsRepository,
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result =
        newsRepository.refreshArticles().fold(
            onSuccess = { Result.success() },
            onFailure = {
                if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            },
        )

    private companion object {
        const val MAX_RETRY_ATTEMPTS = 3
    }
}
