package com.example.newsagreggator.data.location

import com.example.newsagreggator.business.model.Article
import com.example.newsagreggator.business.service.ArticleLocationDetectionStrategy
import com.example.newsagreggator.business.service.CityCatalog
import java.text.Normalizer
import java.util.Locale
import javax.inject.Inject

class AliasArticleLocationDetectionStrategy @Inject constructor(
    private val cityCatalog: CityCatalog,
) : ArticleLocationDetectionStrategy {
    override fun detectRelatedCityIds(article: Article): Set<String> {
        val content = normalize("${article.title} ${article.summary}")
        return cityCatalog.cities
            .filter { city ->
                (city.aliases + city.name).any { alias ->
                    containsPhrase(content, normalize(alias))
                }
            }
            .mapTo(mutableSetOf()) { city -> city.id }
    }

    private fun containsPhrase(content: String, phrase: String): Boolean =
        phrase.isNotEmpty() && " $phrase " in " $content "

    private fun normalize(value: String): String =
        Normalizer.normalize(
            value.lowercase(Locale.forLanguageTag("sr-Latn")),
            Normalizer.Form.NFD,
        )
            .replace(DIACRITICS, "")
            .replace(NON_ALPHANUMERIC, " ")
            .replace(MULTIPLE_SPACES, " ")
            .trim()

    private companion object {
        val DIACRITICS = Regex("\\p{M}+")
        val NON_ALPHANUMERIC = Regex("[^\\p{L}\\p{N}]+")
        val MULTIPLE_SPACES = Regex("\\s+")
    }
}
