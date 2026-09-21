package com.example.newsagreggator.pdf.exporter

import android.graphics.Bitmap
import android.net.Uri
import com.example.newsagreggator.pdf.data.ArticlePdfContent

interface ArticlePdfExporter {
    suspend fun export(
        destination: Uri,
        content: ArticlePdfContent,
        heroImage: Bitmap? = null,
    ): PdfExportResult
}

sealed interface PdfExportResult {
    data object Success : PdfExportResult

    data object InvalidContent : PdfExportResult

    data class DestinationUnavailable(
        val cause: Throwable? = null,
    ) : PdfExportResult

    data class WriteFailed(
        val cause: Throwable,
    ) : PdfExportResult
}
