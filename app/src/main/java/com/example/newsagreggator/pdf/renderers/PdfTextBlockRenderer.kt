package com.example.newsagreggator.pdf.renderers

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.newsagreggator.pdf.PdfPageSession
import com.example.newsagreggator.pdf.util.PdfDocumentStyle
import com.example.newsagreggator.pdf.util.PdfPagePlanner

internal object PdfTextBlockRenderer {
    fun draw(
        session: PdfPageSession,
        text: String,
        paint: TextPaint,
        spacingAfter: Float,
    ) {
        val layout = createLayout(text, paint)
        if (layout.lineCount == 0) {
            session.cursorY += spacingAfter
            return
        }
        if (session.availableHeight < paint.fontSpacing) {
            session.nextPage()
        }

        val lineHeights = List(layout.lineCount) { line ->
            layout.getLineBottom(line) - layout.getLineTop(line)
        }
        val ranges = PdfPagePlanner.planLineRanges(
            lineHeights = lineHeights,
            firstPageAvailableHeight = session.availableHeight.toInt(),
            continuationPageAvailableHeight =
                session.continuationPageHeight.toInt(),
        )

        ranges.forEachIndexed { index, lines ->
            if (index > 0) {
                session.nextPage()
            }
            drawLines(session, layout, lines)
        }
        session.cursorY += spacingAfter
    }

    private fun drawLines(
        session: PdfPageSession,
        layout: StaticLayout,
        lines: IntRange,
    ) {
        val firstLineTop = layout.getLineTop(lines.first)
        val lastLineBottom = layout.getLineBottom(lines.last)
        val chunkHeight = lastLineBottom - firstLineTop

        session.canvas.save()
        session.canvas.clipRect(
            PdfDocumentStyle.PAGE_MARGIN,
            session.cursorY,
            PdfDocumentStyle.PAGE_WIDTH - PdfDocumentStyle.PAGE_MARGIN,
            session.cursorY + chunkHeight,
        )
        session.canvas.translate(
            PdfDocumentStyle.PAGE_MARGIN,
            session.cursorY - firstLineTop,
        )
        layout.draw(session.canvas)
        session.canvas.restore()
        session.cursorY += chunkHeight
    }

    private fun createLayout(
        text: String,
        paint: TextPaint,
    ): StaticLayout = StaticLayout.Builder.obtain(
        text,
        0,
        text.length,
        paint,
        (
            PdfDocumentStyle.PAGE_WIDTH -
                2 * PdfDocumentStyle.PAGE_MARGIN
            ).toInt(),
    )
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setIncludePad(false)
        .setLineSpacing(2f, 1.12f)
        .build()
}