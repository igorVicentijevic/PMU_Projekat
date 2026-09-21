package com.example.newsagreggator.ui.stateholders

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.newsagreggator.R
import com.example.newsagreggator.model.Article
import com.example.newsagreggator.model.NewsCategory
import com.example.newsagreggator.toneanalyzer.ArticleToneDistribution

data class NewsCardUiModel(
    val id: String,
    val imageUrl: String?,
    @param:DrawableRes val placeholderImageResId: Int,
    @param:StringRes val categoryResId: Int,
    val source: String,
    val time: String,
    val title: String,
    val summary: String,
    val url: String,
    val normalizedText: String,
    val relatedCityIds: Set<String>,
    val toneDistribution: ArticleToneDistribution =
        ArticleToneDistribution.Sample,
)

fun Article.toNewsCardUiModel(
    currentTimeMillis: Long = System.currentTimeMillis(),
    toneDistribution: ArticleToneDistribution =
        ArticleToneDistribution.Sample,
): NewsCardUiModel {
    val minutesAgo = (
        (currentTimeMillis - publishedAtEpochMillis)
            .coerceAtLeast(0L) / 60_000L
    )
    return NewsCardUiModel(
        id = id,
        imageUrl = imageUrl,
        placeholderImageResId = category.placeholderImageResId,
        categoryResId = category.labelResId,
        source = source,
        time = minutesAgo.toRelativeTime(),
        title = title,
        summary = summary,
        url = articleUrl,
        normalizedText = normalizedText,
        relatedCityIds = relatedCityIds,
        toneDistribution = toneDistribution,
    )
}

private fun Long.toRelativeTime(): String {
    if (this < MINUTES_PER_HOUR) return "pre $this min"

    val hours = this / MINUTES_PER_HOUR
    val remainingMinutes = this % MINUTES_PER_HOUR
    return if (remainingMinutes == 0L) {
        "pre $hours h"
    } else {
        "pre $hours h $remainingMinutes min"
    }
}

private const val MINUTES_PER_HOUR = 60L

private val NewsCategory.labelResId: Int
    get() = when (this) {
        NewsCategory.Serbia -> R.string.category_serbia
        NewsCategory.World -> R.string.category_world
        NewsCategory.Technology -> R.string.category_technology
        NewsCategory.Business -> R.string.category_business
        NewsCategory.Culture -> R.string.category_culture
        NewsCategory.Sport -> R.string.category_sport
        NewsCategory.Health -> R.string.category_health
    }

private val NewsCategory.placeholderImageResId: Int
    get() = when (this) {
        NewsCategory.Serbia -> R.drawable.news_park
        NewsCategory.Technology -> R.drawable.news_technology
        else -> R.drawable.news_world
    }
