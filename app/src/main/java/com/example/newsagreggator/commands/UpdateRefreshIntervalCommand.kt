package com.example.newsagreggator.commands

import com.example.newsagreggator.background.NewsRefreshScheduler
import com.example.newsagreggator.repository.UserPreferencesRepository
import javax.inject.Inject

class UpdateRefreshIntervalCommand @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : Command<Int, Int>() {
    override suspend fun invoke(input: Int): Int {
        val validInterval = normalize(input)
        userPreferencesRepository.setRefreshInterval(validInterval)
        return validInterval
    }

    fun normalize(intervalMinutes: Int): Int =
        intervalMinutes.coerceAtLeast(
            NewsRefreshScheduler.MIN_INTERVAL_MINUTES
        )
}
