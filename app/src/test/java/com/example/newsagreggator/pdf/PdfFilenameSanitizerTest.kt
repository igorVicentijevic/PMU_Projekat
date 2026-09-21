package com.example.newsagreggator.pdf

import com.example.newsagreggator.pdf.util.PdfFilenameSanitizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfFilenameSanitizerTest {
    @Test
    fun keepsReadableSerbianTitle() {
        assertEquals(
            "Domaći tim razvio novi alat.pdf",
            PdfFilenameSanitizer.fromArticleTitle(
                "Domaći tim razvio novi alat"
            ),
        )
    }

    @Test
    fun removesCharactersForbiddenByCommonFileSystems() {
        assertEquals(
            "Vest naslov test.pdf",
            PdfFilenameSanitizer.fromArticleTitle(
                """Vest: "naslov" / test?"""
            ),
        )
    }

    @Test
    fun doesNotDuplicatePdfExtension() {
        assertEquals(
            "Članak.pdf",
            PdfFilenameSanitizer.fromArticleTitle("Članak.PDF"),
        )
    }

    @Test
    fun fallsBackForBlankOrInvalidTitle() {
        assertEquals(
            "article.pdf",
            PdfFilenameSanitizer.fromArticleTitle("""<>:"/\|?*"""),
        )
    }

    @Test
    fun limitsSuggestedFilenameLength() {
        val filename = PdfFilenameSanitizer.fromArticleTitle("a".repeat(120))

        assertTrue(filename.length <= 84)
        assertTrue(filename.endsWith(".pdf"))
    }
}
