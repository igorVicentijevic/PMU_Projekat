package com.example.newsagreggator.pdf

import com.example.newsagreggator.pdf.util.PdfPagePlanner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PdfPagePlannerTest {
    @Test
    fun keepsLinesOnOnePageWhenTheyFit() {
        assertEquals(
            listOf(0..2),
            PdfPagePlanner.planLineRanges(
                lineHeights = listOf(20, 20, 20),
                firstPageAvailableHeight = 60,
                continuationPageAvailableHeight = 100,
            ),
        )
    }

    @Test
    fun usesRemainingFirstPageThenFullContinuationPages() {
        assertEquals(
            listOf(0..1, 2..4, 5..5),
            PdfPagePlanner.planLineRanges(
                lineHeights = List(6) { 20 },
                firstPageAvailableHeight = 40,
                continuationPageAvailableHeight = 60,
            ),
        )
    }

    @Test
    fun oversizedLineStillMakesProgress() {
        assertEquals(
            listOf(0..0, 1..1),
            PdfPagePlanner.planLineRanges(
                lineHeights = listOf(120, 20),
                firstPageAvailableHeight = 50,
                continuationPageAvailableHeight = 50,
            ),
        )
    }

    @Test
    fun rejectsInvalidPageHeight() {
        assertThrows(IllegalArgumentException::class.java) {
            PdfPagePlanner.planLineRanges(
                lineHeights = listOf(20),
                firstPageAvailableHeight = 0,
                continuationPageAvailableHeight = 100,
            )
        }
    }
}
