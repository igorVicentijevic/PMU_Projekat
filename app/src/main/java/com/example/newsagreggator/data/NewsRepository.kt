package com.example.newsagreggator.data

import com.example.newsagreggator.ui.model.NewsCardUiModel

interface NewsRepository {
    fun getArticles(): List<NewsCardUiModel>
}
