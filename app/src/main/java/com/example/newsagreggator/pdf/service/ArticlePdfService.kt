package com.example.newsagreggator.pdf.service

import android.net.Uri
import com.example.newsagreggator.pdf.ArticlePdfContentFactory
import com.example.newsagreggator.pdf.exporter.ArticlePdfExporter
import com.example.newsagreggator.pdf.exporter.PdfExportResult
import com.example.newsagreggator.pdf.util.ArticlePdfImageLoader
import com.example.newsagreggator.pdf.util.PdfFilenameSanitizer
import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import javax.inject.Inject

//Entrypoint to pdf codebase
class ArticlePdfService @Inject constructor(
    private val contentFactory: ArticlePdfContentFactory,
    private val imageLoader: ArticlePdfImageLoader,
    private val exporter: ArticlePdfExporter,
) {
    fun suggestedFilename(article: NewsCardUiModel): String =
        PdfFilenameSanitizer.fromArticleTitle(article.title)

    suspend fun export(
        article: NewsCardUiModel,
        destination: Uri,
    ): PdfExportResult {
        val content = contentFactory.create(article)
        val heroImage = imageLoader.load(
            imageUrl = article.imageUrl,
            fallbackResId = article.placeholderImageResId,
        )
        return exporter.export(
            destination = destination,
            content = content,
            heroImage = heroImage,
        )
    }
}