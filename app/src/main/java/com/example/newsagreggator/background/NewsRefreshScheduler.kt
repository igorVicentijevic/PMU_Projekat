package com.example.newsagreggator.background

interface NewsRefreshScheduler {
    fun schedule(intervalMinutes: Int)

    companion object {
        const val MIN_INTERVAL_MINUTES = 15
    }
}