package com.example.newsagreggator.data

import com.example.newsagreggator.domain.model.Article
import kotlinx.coroutines.delay

class SampleNewsRepository : NewsRepository {
    private var articles = createSampleNewsArticles()

    override fun getArticles(): List<Article> = articles

    override suspend fun refreshArticles(): Result<List<Article>> {
        delay(700)
        articles = createSampleNewsArticles()
        return Result.success(articles)
    }
}
