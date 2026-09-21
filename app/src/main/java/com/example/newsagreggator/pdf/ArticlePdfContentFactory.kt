package com.example.newsagreggator.pdf

import android.content.Context
import com.example.newsagreggator.R
import com.example.newsagreggator.pdf.data.ArticlePdfContent
import com.example.newsagreggator.pdf.data.ArticlePdfLabels
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import java.util.Date
import javax.inject.Inject

class ArticlePdfContentFactory @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun create(
        article: NewsCardUiModel,
        exportedAtEpochMillis: Long = System.currentTimeMillis(),
    ): ArticlePdfContent = ArticlePdfContent(
        title = article.title,
        summary = article.summary,
        source = article.source,
        category = context.getString(article.categoryResId),
        publishedTime = article.time,
        originalUrl = article.url,
        exportedAt = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM,
            DateFormat.SHORT,
        ).format(Date(exportedAtEpochMillis)),
        labels = ArticlePdfLabels(
            appName = context.getString(R.string.app_name),
            originalArticle = context.getString(
                R.string.pdf_original_article
            ),
            exportedAt = context.getString(R.string.pdf_exported_at),
            savedWithApp = context.getString(R.string.pdf_saved_with_app),
            page = context.getString(R.string.pdf_page),
            summaryUnavailable = context.getString(
                R.string.pdf_summary_unavailable
            ),
        ),
    )
}
