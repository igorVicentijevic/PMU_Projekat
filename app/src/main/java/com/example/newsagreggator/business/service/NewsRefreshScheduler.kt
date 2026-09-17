package com.example.newsagreggator.business.service

interface NewsRefreshScheduler {
    fun schedule(intervalMinutes: Int)

    companion object {
        const val MIN_INTERVAL_MINUTES = 15
    }
}
