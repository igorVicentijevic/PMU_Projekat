package com.example.newsagreggator.data.notification

import com.example.newsagreggator.business.model.Article
import com.example.newsagreggator.business.service.BreakingNewsStrategy
import com.example.newsagreggator.business.service.TextNormalizer
import javax.inject.Inject

class KeywordBreakingNewsStrategy @Inject constructor(
    private val textNormalizer: TextNormalizer,
) : BreakingNewsStrategy {
    override fun isBreaking(article: Article): Boolean {
        val normalizedText = article.normalizedText.ifBlank {
            textNormalizer.normalize("${article.title} ${article.summary}")
        }
        val words = normalizedText.split(' ').toSet()
        return BREAKING_NEWS_KEYWORDS.any(words::contains)
    }

    private companion object {
        val BREAKING_NEWS_KEYWORDS = setOf("hitno", "vanredno")
    }
}
