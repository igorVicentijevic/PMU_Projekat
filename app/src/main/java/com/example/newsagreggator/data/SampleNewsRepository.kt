package com.example.newsagreggator.data

import com.example.newsagreggator.ui.model.NewsCardUiModel
import com.example.newsagreggator.ui.model.sampleNewsArticles

class SampleNewsRepository : NewsRepository {
    override fun getArticles(): List<NewsCardUiModel> = sampleNewsArticles
}
