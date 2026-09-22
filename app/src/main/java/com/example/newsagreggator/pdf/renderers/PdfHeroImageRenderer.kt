package com.example.newsagreggator.pdf.renderers

import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import com.example.newsagreggator.pdf.util.PdfDocumentStyle
import kotlin.math.max

internal object PdfHeroImageRenderer {
    fun draw(
        session: PdfPageSession,
        bitmap: Bitmap,
    ) {
        session.ensureSpace(
            PdfDocumentStyle.HERO_HEIGHT +
                PdfDocumentStyle.SECTION_SPACING
        )
        val destination = RectF(
            PdfDocumentStyle.PAGE_MARGIN,
            session.cursorY,
            PdfDocumentStyle.PAGE_WIDTH - PdfDocumentStyle.PAGE_MARGIN,
            session.cursorY + PdfDocumentStyle.HERO_HEIGHT,
        )
        val clipPath = Path().apply {
            addRoundRect(destination, 18f, 18f, Path.Direction.CW)
        }

        session.canvas.save()
        session.canvas.clipPath(clipPath)
        session.canvas.drawBitmap(
            bitmap,
            centerCropSource(bitmap, destination),
            destination,
            PdfDocumentStyle.imagePaint(),
        )
        session.canvas.restore()
        session.cursorY +=
            PdfDocumentStyle.HERO_HEIGHT +
                PdfDocumentStyle.SECTION_SPACING
    }

    private fun centerCropSource(
        bitmap: Bitmap,
        destination: RectF,
    ): Rect {
        val destinationRatio = destination.width() / destination.height()
        val sourceRatio = bitmap.width.toFloat() / bitmap.height

        return if (sourceRatio > destinationRatio) {
            val sourceWidth = (bitmap.height * destinationRatio).toInt()
            val left = max(0, (bitmap.width - sourceWidth) / 2)
            Rect(left, 0, left + sourceWidth, bitmap.height)
        } else {
            val sourceHeight = (bitmap.width / destinationRatio).toInt()
            val top = max(0, (bitmap.height - sourceHeight) / 2)
            Rect(0, top, bitmap.width, top + sourceHeight)
        }
    }
}