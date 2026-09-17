package com.example.newsagreggator.data.sample

import com.example.newsagreggator.business.model.Article
import com.example.newsagreggator.business.model.NewsCategory
import com.example.newsagreggator.data.text.SerbianTextNormalizer

private const val MINUTE_MILLIS = 60_000L
private val textNormalizer = SerbianTextNormalizer()

fun createSampleNewsArticles(
    currentTimeMillis: Long = System.currentTimeMillis(),
): List<Article> = listOf(
    Article(
        id = "1",
        title = "Novi park uz Savu otvara prostor za sport, odmor i kulturu",
        summary =
            "Zelena oaza sa više od 300 novih stabala biće otvorena za posetioce ovog vikenda.",
        source = "Danas",
        category = NewsCategory.Serbia,
        publishedAtEpochMillis = currentTimeMillis - 12 * MINUTE_MILLIS,
        imageUrl = null,
        articleUrl = "https://www.danas.rs/",
        relatedCityIds = setOf("belgrade"),
    ),
    Article(
        id = "2",
        title = "Domaći tim razvio alat koji štedi sate svakodnevnog rada",
        summary =
            "Platforma zasnovana na veštačkoj inteligenciji privukla je pažnju regionalnih kompanija.",
        source = "Netokracija",
        category = NewsCategory.Technology,
        publishedAtEpochMillis = currentTimeMillis - 28 * MINUTE_MILLIS,
        imageUrl = null,
        articleUrl = "https://www.netokracija.rs/",
    ),
    Article(
        id = "3",
        title = "Evropski gradovi ubrzavaju prelazak na čistu energiju",
        summary =
            "Novi plan povezuje javni prevoz, energetsku efikasnost i lokalnu proizvodnju energije.",
        source = "Reuters",
        category = NewsCategory.World,
        publishedAtEpochMillis = currentTimeMillis - 41 * MINUTE_MILLIS,
        imageUrl = null,
        articleUrl = "https://www.reuters.com/world/",
    ),
).map { article ->
    article.copy(
        normalizedText = textNormalizer.normalize(
            "${article.title} ${article.summary} ${article.source}"
        )
    )
}
