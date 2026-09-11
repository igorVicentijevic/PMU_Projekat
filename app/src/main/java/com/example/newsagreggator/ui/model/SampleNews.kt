package com.example.newsagreggator.ui.model

import com.example.newsagreggator.R

val sampleNewsArticles = listOf(
    NewsCardUiModel(
        id = 1,
        imageResId = R.drawable.news_park,
        categoryResId = R.string.category_serbia,
        sourceResId = R.string.news_source_danas,
        timeResId = R.string.news_time_12_minutes_short,
        titleResId = R.string.news_park_title,
        summaryResId = R.string.news_park_summary,
    ),
    NewsCardUiModel(
        id = 2,
        imageResId = R.drawable.news_technology,
        categoryResId = R.string.category_technology,
        sourceResId = R.string.news_source_netokracija,
        timeResId = R.string.news_time_28_minutes_short,
        titleResId = R.string.news_technology_title,
        summaryResId = R.string.news_technology_summary,
    ),
    NewsCardUiModel(
        id = 3,
        imageResId = R.drawable.news_world,
        categoryResId = R.string.category_world,
        sourceResId = R.string.news_source_reuters,
        timeResId = R.string.news_time_41_minutes_short,
        titleResId = R.string.news_world_title,
        summaryResId = R.string.news_world_summary,
    ),
)
