package com.example.newsagreggator.pdf.renderers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.example.newsagreggator.pdf.data.ArticlePdfContent
import com.example.newsagreggator.pdf.util.PdfDocumentStyle

internal object PdfHeaderFooterRenderer {
    fun drawHeader(
        canvas: Canvas,
        content: ArticlePdfContent,
    ) {
        val brandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = PdfDocumentStyle.tokGreen
            textSize = 19f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText(
            content.labels.appName.uppercase(),
            PdfDocumentStyle.PAGE_MARGIN,
            46f,
            brandPaint,
        )

        val categoryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val category = content.category.uppercase()
        val badgeWidth = categoryPaint.measureText(category) + 22f
        val badge = RectF(
            PdfDocumentStyle.PAGE_WIDTH -
                    PdfDocumentStyle.PAGE_MARGIN -
                    badgeWidth,
            28f,
            PdfDocumentStyle.PAGE_WIDTH - PdfDocumentStyle.PAGE_MARGIN,
            52f,
        )
        canvas.drawRoundRect(badge, 8f, 8f, Paint().apply {
            color = PdfDocumentStyle.tokGreen
            style = Paint.Style.FILL
        })
        canvas.drawText(
            category,
            badge.left + 11f,
            badge.centerY() - (
                categoryPaint.descent() + categoryPaint.ascent()
            ) / 2f,
            categoryPaint,
        )
        canvas.drawLine(
            PdfDocumentStyle.PAGE_MARGIN,
            66f,
            PdfDocumentStyle.PAGE_WIDTH - PdfDocumentStyle.PAGE_MARGIN,
            66f,
            PdfDocumentStyle.dividerPaint(),
        )
    }

    fun drawFooter(
        canvas: Canvas,
        content: ArticlePdfContent,
        pageNumber: Int,
    ) {
        canvas.drawLine(
            PdfDocumentStyle.PAGE_MARGIN,
            PdfDocumentStyle.FOOTER_DIVIDER_Y,
            PdfDocumentStyle.PAGE_WIDTH - PdfDocumentStyle.PAGE_MARGIN,
            PdfDocumentStyle.FOOTER_DIVIDER_Y,
            PdfDocumentStyle.dividerPaint(),
        )
        val paint = PdfDocumentStyle.captionPaint()
        canvas.drawText(
            content.source,
            PdfDocumentStyle.PAGE_MARGIN,
            PdfDocumentStyle.FOOTER_TEXT_Y,
            paint,
        )
        val pageText = "${content.labels.page} $pageNumber"
        canvas.drawText(
            pageText,
            PdfDocumentStyle.PAGE_WIDTH -
                PdfDocumentStyle.PAGE_MARGIN -
                paint.measureText(pageText),
            PdfDocumentStyle.FOOTER_TEXT_Y,
            paint,
        )
    }
}