package com.example.newsagreggator.digest.strategy

import com.example.newsagreggator.ui.stateholders.NewsCardUiModel
import javax.inject.Inject

class WordCountDigestReadingTimeStrategy @Inject constructor() :
    DigestReadingTimeStrategy {

    override fun estimateMinutes(articles: List<NewsCardUiModel>): Int {
        val wordCount = articles.sumOf { article ->
            countWords(article.title) + countWords(article.summary)
        }
        if (wordCount == 0) {
            return 0
        }

        return (wordCount + WORDS_PER_MINUTE - 1) / WORDS_PER_MINUTE
    }

    private fun countWords(text: String): Int =
        WORD_PATTERN.findAll(text).count()

    private companion object {
        const val WORDS_PER_MINUTE = 150
        val WORD_PATTERN = Regex("""\S+""")
    }
}
