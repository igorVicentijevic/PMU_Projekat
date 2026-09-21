package com.example.newsagreggator.pdf

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import com.example.newsagreggator.pdf.data.ArticlePdfContent
import com.example.newsagreggator.pdf.renderers.PdfHeaderFooterRenderer
import com.example.newsagreggator.pdf.util.PdfDocumentStyle

internal class PdfPageSession(
    private val document: PdfDocument,
    private val content: ArticlePdfContent,
) {
    private var pageNumber = 0
    private lateinit var page: PdfDocument.Page

    lateinit var canvas: Canvas
        private set

    var cursorY: Float = PdfDocumentStyle.CONTENT_TOP

    val availableHeight: Float
        get() = contentBottom - cursorY

    val continuationPageHeight: Float
        get() = contentBottom - PdfDocumentStyle.CONTENT_TOP

    private val contentBottom: Float
        get() = PdfDocumentStyle.FOOTER_DIVIDER_Y - 18f

    fun start() {
        startPage()
    }

    fun ensureSpace(requiredHeight: Float) {
        if (requiredHeight > availableHeight) {
            nextPage()
        }
    }

    fun nextPage() {
        finishPage()
        startPage()
    }

    fun finish() {
        finishPage()
    }

    private fun startPage() {
        pageNumber += 1
        page = document.startPage(
            PdfDocument.PageInfo.Builder(
                PdfDocumentStyle.PAGE_WIDTH,
                PdfDocumentStyle.PAGE_HEIGHT,
                pageNumber,
            ).create()
        )
        canvas = page.canvas
        canvas.drawColor(Color.WHITE)
        PdfHeaderFooterRenderer.drawHeader(canvas, content)
        cursorY = PdfDocumentStyle.CONTENT_TOP
    }

    private fun finishPage() {
        PdfHeaderFooterRenderer.drawFooter(
            canvas = canvas,
            content = content,
            pageNumber = pageNumber,
        )
        document.finishPage(page)
    }
}
