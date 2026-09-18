package com.example.newsagreggator.util

import org.junit.Assert.assertEquals
import org.junit.Test

class SerbianTextNormalizerTest {
    private val normalizer = SerbianTextNormalizer()

    @Test
    fun normalizesSerbianLatinText() {
        assertEquals(
            "cacak nis djerdap",
            normalizer.normalize("Čačak, NIŠ — Đerdap!"),
        )
    }

    @Test
    fun normalizesSerbianCyrillicText() {
        assertEquals(
            "novi sad i nis",
            normalizer.normalize("Нови Сад и Ниш"),
        )
    }

    @Test
    fun collapsesWhitespaceAndPreservesNumbers() {
        assertEquals(
            "vesti 2026 danas",
            normalizer.normalize("  Vesti   2026.\nDanas "),
        )
    }
}
