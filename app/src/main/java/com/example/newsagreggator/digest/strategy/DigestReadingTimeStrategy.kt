package com.example.newsagreggator.digest.strategy

import com.example.newsagreggator.ui.stateholders.NewsCardUiModel

fun interface DigestReadingTimeStrategy {
    fun estimateMinutes(articles: List<NewsCardUiModel>): Int
}
