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
            value
                .lowercase(Locale.forLanguageTag("sr"))
                .toCanonicalLatin(),
            Normalizer.Form.NFD,
        )
            .replace(DIACRITICS, "")
            .replace(NON_ALPHANUMERIC, " ")
            .replace(MULTIPLE_SPACES, " ")
            .trim()

    private fun String.toCanonicalLatin(): String = buildString {
        this@toCanonicalLatin.forEach { character ->
            append(SERBIAN_CHARACTER_REPLACEMENTS[character] ?: character)
        }
    }

    private companion object {
        val DIACRITICS = Regex("\\p{M}+")
        val NON_ALPHANUMERIC = Regex("[^\\p{L}\\p{N}]+")
        val MULTIPLE_SPACES = Regex("\\s+")
        val SERBIAN_CHARACTER_REPLACEMENTS = mapOf(
            'а' to "a",
            'б' to "b",
            'в' to "v",
            'г' to "g",
            'д' to "d",
            'ђ' to "dj",
            'е' to "e",
            'ж' to "z",
            'з' to "z",
            'и' to "i",
            'ј' to "j",
            'к' to "k",
            'л' to "l",
            'љ' to "lj",
            'м' to "m",
            'н' to "n",
            'њ' to "nj",
            'о' to "o",
            'п' to "p",
            'р' to "r",
            'с' to "s",
            'т' to "t",
            'ћ' to "c",
            'у' to "u",
            'ф' to "f",
            'х' to "h",
            'ц' to "c",
            'ч' to "c",
            'џ' to "dz",
            'ш' to "s",
            'đ' to "dj",
        )
    }
}
