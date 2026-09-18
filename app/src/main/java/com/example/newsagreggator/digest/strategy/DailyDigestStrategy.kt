package com.example.newsagreggator.digest.strategy

import com.example.newsagreggator.ui.stateholders.NewsCardUiModel

fun interface DailyDigestStrategy {
    fun select(
        articles: List<NewsCardUiModel>,
        followedCategories: Set<Int>,
    ): List<NewsCardUiModel>
}
