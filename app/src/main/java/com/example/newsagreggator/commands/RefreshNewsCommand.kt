package com.example.newsagreggator.commands

import com.example.newsagreggator.repository.NewsRepository
import javax.inject.Inject

class RefreshNewsCommand @Inject constructor(
    private val newsRepository: NewsRepository,
) : Command<RefreshNewsCommand.Policy, Result<Unit>>() {
    override suspend fun invoke(input: Policy): Result<Unit> =
        when (input) {
            Policy.Force -> newsRepository.refreshArticles()
            Policy.IfStale -> newsRepository.refreshArticlesIfStale(
                STALE_AFTER_MILLIS
            )
        }

    fun shouldRefresh(
        lastSuccessfulRefreshEpochMillis: Long?,
        currentTimeMillis: Long = System.currentTimeMillis(),
    ): Boolean = lastSuccessfulRefreshEpochMillis == null ||
        currentTimeMillis - lastSuccessfulRefreshEpochMillis >=
        STALE_AFTER_MILLIS

    enum class Policy {
        Force,
        IfStale,
    }

    private companion object {
        const val STALE_AFTER_MILLIS = 5 * 60_000L
    }
}
