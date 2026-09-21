package com.example.newsagreggator.pdf.exporter

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.newsagreggator.pdf.renderers.ArticlePdfRenderer
import com.example.newsagreggator.pdf.data.ArticlePdfContent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidArticlePdfExporter @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val renderer: ArticlePdfRenderer,
) : ArticlePdfExporter {
    override suspend fun export(
        destination: Uri,
        content: ArticlePdfContent,
        heroImage: Bitmap?,
    ): PdfExportResult = withContext(Dispatchers.IO) {
        if (content.title.isBlank() || content.originalUrl.isBlank()) {
            return@withContext PdfExportResult.InvalidContent
        }

        val outputStream = try {
            context.contentResolver.openOutputStream(destination, "w")
        } catch (error: FileNotFoundException) {
            return@withContext PdfExportResult.DestinationUnavailable(error)
        } catch (error: SecurityException) {
            return@withContext PdfExportResult.DestinationUnavailable(error)
        }

        if (outputStream == null) {
            return@withContext PdfExportResult.DestinationUnavailable()
        }

        try {
            outputStream.use { output ->
                renderer.write(content, heroImage, output)
            }
            PdfExportResult.Success
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: IOException) {
            PdfExportResult.WriteFailed(error)
        } catch (error: IllegalStateException) {
            PdfExportResult.WriteFailed(error)
        }
    }
}