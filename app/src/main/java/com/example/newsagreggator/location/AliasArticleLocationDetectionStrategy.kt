package com.example.newsagreggator.location

import com.example.newsagreggator.model.Article
import com.example.newsagreggator.util.TextNormalizer
import javax.inject.Inject

class AliasArticleLocationDetectionStrategy @Inject constructor(
    private val cityCatalog: CityCatalog,
    private val textNormalizer: TextNormalizer,
) : ArticleLocationDetectionStrategy {
    private val normalizedAliasesByCityId = cityCatalog.cities.associate { city ->
        city.id to (city.aliases + city.name)
            .map(textNormalizer::normalize)
            .filter(String::isNotEmpty)
    }

    override fun detectRelatedCityIds(article: Article): Set<String> {
        check(article.normalizedText.isNotBlank()) {
            "Article text must be normalized before location detection"
        }
        return cityCatalog.cities
            .filter { city ->
                normalizedAliasesByCityId.getValue(city.id).any { alias ->
                    containsPhrase(article.normalizedText, alias)
                }
            }
            .mapTo(mutableSetOf()) { city -> city.id }
    }

    private fun containsPhrase(content: String, phrase: String): Boolean =
        phrase.isNotEmpty() && " $phrase " in " $content "
}
