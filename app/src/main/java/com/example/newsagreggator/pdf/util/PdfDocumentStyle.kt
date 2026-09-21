package com.example.newsagreggator.pdf.util

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint

internal object PdfDocumentStyle {
    const val PAGE_WIDTH = 595
    const val PAGE_HEIGHT = 842
    const val PAGE_MARGIN = 48f
    const val CONTENT_TOP = 86f
    const val HERO_HEIGHT = 210f
    const val SECTION_SPACING = 18f
    const val FOOTER_DIVIDER_Y = 798f
    const val FOOTER_TEXT_Y = 820f
    val tokGreen: Int = Color.rgb(28, 112, 82)

    fun metadataPaint() = textPaint(
        color = tokGreen,
        size = 11f,
        bold = true,
    )

    fun titlePaint() = textPaint(
        color = Color.rgb(25, 31, 28),
        size = 25f,
        bold = true,
    )

    fun bodyPaint() = textPaint(
        color = Color.rgb(56, 64, 60),
        size = 14f,
    )

    fun sourceLabelPaint() = textPaint(
        color = Color.rgb(25, 31, 28),
        size = 12f,
        bold = true,
    )

    fun linkPaint() = textPaint(
        color = tokGreen,
        size = 10f,
    )

    fun captionPaint() = textPaint(
        color = Color.rgb(100, 108, 104),
        size = 9f,
    )

    fun dividerPaint() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(220, 225, 222)
        strokeWidth = 1f
    }

    fun imagePaint() = Paint(
        Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
    )

    private fun textPaint(
        color: Int,
        size: Float,
        bold: Boolean = false,
    ) = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textSize = size
        typeface = Typeface.create(
            Typeface.DEFAULT,
            if (bold) Typeface.BOLD else Typeface.NORMAL,
        )
    }
}