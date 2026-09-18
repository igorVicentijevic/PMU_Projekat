package com.example.newsagreggator.digest.strategy

import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import javax.inject.Inject

class FollowedCategoriesDailyDigestStrategy @Inject constructor() :
    DailyDigestStrategy {

    override fun select(
        articles: List<NewsCardUiModel>,
        followedCategories: Set<Int>,
    ): List<NewsCardUiModel> =
        articles
            .filter { article ->
                article.categoryResId in followedCategories
            }
            .take(MAX_ARTICLES)

    private companion object {
        const val MAX_ARTICLES = 5
    }
}
