package com.example.newsagreggator.data

import com.example.newsagreggator.ui.model.NewsCardUiModel
import com.example.newsagreggator.ui.model.sampleNewsArticles
import kotlinx.coroutines.delay

class SampleNewsRepository : NewsRepository {
    override fun getArticles(): List<NewsCardUiModel> = sampleNewsArticles

    override suspend fun refreshArticles(): Result<List<NewsCardUiModel>> {
        delay(700)
        return Result.success(sampleNewsArticles)
    }
}
