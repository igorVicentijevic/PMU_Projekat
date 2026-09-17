package com.example.newsagreggator.sample

import com.example.newsagreggator.repository.NewsRepository
import com.example.newsagreggator.repository.NewsSnapshot
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
