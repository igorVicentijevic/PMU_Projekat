package com.example.newsagreggator.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class NewsCardUiModel(
    val id: Int,
    @DrawableRes val imageResId: Int,
    @StringRes val categoryResId: Int,
    @StringRes val sourceResId: Int,
    @StringRes val timeResId: Int,
    @StringRes val titleResId: Int,
    @StringRes val summaryResId: Int,
    val url: String,
)
