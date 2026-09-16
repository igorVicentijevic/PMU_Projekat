package com.example.newsagreggator.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

class SampleNewsRepository : NewsRepository {
    private val state = MutableStateFlow(
        NewsSnapshot(
            articles = createSampleNewsArticles(),
            lastSuccessfulRefreshEpochMillis = null,
        )
    )
    override val news = state

    override suspend fun refreshArticles(): Result<Unit> {
        delay(700)
        state.value = NewsSnapshot(
            articles = createSampleNewsArticles(),
            lastSuccessfulRefreshEpochMillis = System.currentTimeMillis(),
        )
        return Result.success(Unit)
    }
}
