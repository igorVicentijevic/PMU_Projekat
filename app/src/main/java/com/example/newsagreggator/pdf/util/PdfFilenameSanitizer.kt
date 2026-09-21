package com.example.newsagreggator.pdf.util

import java.text.Normalizer

object PdfFilenameSanitizer {
    private const val DEFAULT_NAME = "article"
    private const val MAX_BASE_LENGTH = 80
    private const val PDF_EXTENSION_LENGTH = 4
    private val invalidCharacters = setOf(
        '<', '>', ':', '"', '/', '\\', '|', '?', '*'
    )

    fun fromArticleTitle(title: String): String {
        val sanitized = Normalizer.normalize(
            title.trim().removePdfExtension(),
            Normalizer.Form.NFKC,
        )
            .filterNot { character ->
                character.code < 32 || character in invalidCharacters
            }
            .replace(Regex("\\s+"), " ")
            .trim(' ', '.')
            .take(MAX_BASE_LENGTH)
            .trimEnd(' ', '.')
            .ifBlank { DEFAULT_NAME }

        return "$sanitized.pdf"
    }

    private fun String.removePdfExtension(): String =
        if (endsWith(".pdf", ignoreCase = true)) {
            dropLast(PDF_EXTENSION_LENGTH)
        } else {
            this
        }
}