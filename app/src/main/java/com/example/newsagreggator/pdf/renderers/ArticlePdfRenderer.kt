package com.example.newsagreggator.pdf.renderers

import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import com.example.newsagreggator.pdf.data.ArticlePdfContent
import com.example.newsagreggator.pdf.util.PdfDocumentStyle
import java.io.OutputStream
import javax.inject.Inject


//Main, orchestration renderer
class ArticlePdfRenderer @Inject constructor() {
    fun write(
        content: ArticlePdfContent,
        heroImage: Bitmap?,
        outputStream: OutputStream,
    ) {
        val document = PdfDocument()
        try {
            val session = PdfPageSession(document, content)
            session.start()

            heroImage?.let { PdfHeroImageRenderer.draw(session, it) }

            drawArticle(session, content)

            session.finish()
            document.writeTo(outputStream)
        } finally {
            document.close()
        }
    }

    private fun drawArticle(
        session: PdfPageSession,
        content: ArticlePdfContent,
    ) {
        drawText(
            session = session,
            text = listOf(
                content.source,
                content.publishedTime,
                content.category,
            ).filter(String::isNotBlank).joinToString("  •  "),
            paint = PdfDocumentStyle.metadataPaint(),
            spacingAfter = 12f,
        )
        drawText(
            session = session,
            text = content.title,
            paint = PdfDocumentStyle.titlePaint(),
            spacingAfter = 18f,
        )
        drawText(
            session = session,
            text = content.summary.ifBlank {
                content.labels.summaryUnavailable
            },
            paint = PdfDocumentStyle.bodyPaint(),
            spacingAfter = 24f,
        )
        drawSource(session, content)
    }

    private fun drawSource(
        session: PdfPageSession,
        content: ArticlePdfContent,
    ) {
        session.ensureSpace(80f)
        session.canvas.drawLine(
            PdfDocumentStyle.PAGE_MARGIN,
            session.cursorY,
            PdfDocumentStyle.PAGE_WIDTH - PdfDocumentStyle.PAGE_MARGIN,
            session.cursorY,
            PdfDocumentStyle.dividerPaint(),
        )
        session.cursorY += 17f
        drawText(
            session,
            content.labels.originalArticle,
            PdfDocumentStyle.sourceLabelPaint(),
            5f,
        )
        drawText(
            session,
            content.originalUrl,
            PdfDocumentStyle.linkPaint(),
            10f,
        )
        drawText(
            session,
            "${content.labels.exportedAt} ${content.exportedAt}",
            PdfDocumentStyle.captionPaint(),
            4f,
        )
        drawText(
            session,
            content.labels.savedWithApp,
            PdfDocumentStyle.captionPaint(),
            0f,
        )
    }

    private fun drawText(
        session: PdfPageSession,
        text: String,
        paint: TextPaint,
        spacingAfter: Float,
    ) {
        PdfTextBlockRenderer.draw(
            session = session,
            text = text,
            paint = paint,
            spacingAfter = spacingAfter,
        )
    }
}