package com.example.newsagreggator.articlegrouping

import com.example.newsagreggator.model.Article
import com.example.newsagreggator.model.NewsCategory
import javax.inject.Inject

class MockArticleGroupingStrategy @Inject constructor() :
    ArticleGroupingStrategy {

    override suspend fun group(
        articles: List<Article>,
    ): List<ArticleGroup> {
        val now = System.currentTimeMillis()

        return listOf(
            ArticleGroup(
                id = "mock-group-sava-park",
                title = "Otvaranje novog parka uz Savu",
                articles = listOf(
                    mockArticle(
                        id = "mock-sava-park-danas",
                        title = "Novi park uz Savu otvara se ovog vikenda",
                        summary = "Posetiocima će biti dostupni sportski tereni, zelene površine i prostor za kulturne događaje.",
                        source = "Danas",
                        category = NewsCategory.Serbia,
                        publishedAtEpochMillis = now - 12 * MINUTE_MILLIS,
                        articleUrl = "https://www.danas.rs/",
                    ),
                    mockArticle(
                        id = "mock-sava-park-rts",
                        title = "Beograd dobija novu zelenu oazu uz Savu",
                        summary = "Gradske službe završile su uređenje parka sa više od 300 stabala i sadržajima za porodice.",
                        source = "RTS",
                        category = NewsCategory.Serbia,
                        publishedAtEpochMillis = now - 20 * MINUTE_MILLIS,
                        articleUrl = "https://www.rts.rs/",
                    ),
                    mockArticle(
                        id = "mock-sava-park-n1",
                        title = "Završen park uz Savu sa sportskim i kulturnim sadržajima",
                        summary = "Novi javni prostor biće otvoren za građane nakon završetka poslednje faze radova.",
                        source = "N1",
                        category = NewsCategory.Serbia,
                        publishedAtEpochMillis = now - 31 * MINUTE_MILLIS,
                        articleUrl = "https://n1info.rs/",
                    ),
                ),
            ),
            ArticleGroup(
                id = "mock-group-clean-energy",
                title = "Evropski plan za prelazak na čistu energiju",
                articles = listOf(
                    mockArticle(
                        id = "mock-clean-energy-reuters",
                        title = "Evropski gradovi ubrzavaju prelazak na čistu energiju",
                        summary = "Novi plan povezuje javni prevoz, energetsku efikasnost i lokalnu proizvodnju energije.",
                        source = "Reuters",
                        category = NewsCategory.World,
                        publishedAtEpochMillis = now - 18 * MINUTE_MILLIS,
                        articleUrl = "https://www.reuters.com/world/europe/",
                    ),
                    mockArticle(
                        id = "mock-clean-energy-euronews",
                        title = "Gradovi predstavili zajednički plan za obnovljive izvore",
                        summary = "Evropske prestonice dogovorile su nove ciljeve za smanjenje emisija i čistiji javni prevoz.",
                        source = "Euronews",
                        category = NewsCategory.World,
                        publishedAtEpochMillis = now - 27 * MINUTE_MILLIS,
                        articleUrl = "https://www.euronews.com/green",
                    ),
                    mockArticle(
                        id = "mock-clean-energy-n1",
                        title = "Čista energija u centru novog evropskog dogovora",
                        summary = "Dogovor predviđa ulaganja u energetsku efikasnost, solarne sisteme i električni prevoz.",
                        source = "N1",
                        category = NewsCategory.World,
                        publishedAtEpochMillis = now - 39 * MINUTE_MILLIS,
                        articleUrl = "https://n1info.rs/svet/",
                    ),
                ),
            ),
            ArticleGroup(
                id = "mock-group-ai-tool",
                title = "Domaći tim predstavio novi AI alat",
                articles = listOf(
                    mockArticle(
                        id = "mock-ai-tool-netokracija",
                        title = "Domaći tim razvio AI alat koji štedi sate rada",
                        summary = "Platforma automatizuje svakodnevne poslovne zadatke i već privlači regionalne kompanije.",
                        source = "Netokracija",
                        category = NewsCategory.Technology,
                        publishedAtEpochMillis = now - 15 * MINUTE_MILLIS,
                        articleUrl = "https://www.netokracija.rs/",
                    ),
                    mockArticle(
                        id = "mock-ai-tool-rts",
                        title = "Srpski inženjeri predstavili platformu zasnovanu na veštačkoj inteligenciji",
                        summary = "Novi alat namenjen je kompanijama koje žele da ubrzaju ponavljajuće administrativne poslove.",
                        source = "RTS",
                        category = NewsCategory.Technology,
                        publishedAtEpochMillis = now - 24 * MINUTE_MILLIS,
                        articleUrl = "https://www.rts.rs/magazin/tehnologija.html",
                    ),
                    mockArticle(
                        id = "mock-ai-tool-danas",
                        title = "Nova domaća AI platforma privukla pažnju kompanija",
                        summary = "Razvojni tim najavio je širenje platforme i podršku za dodatne poslovne procese.",
                        source = "Danas",
                        category = NewsCategory.Technology,
                        publishedAtEpochMillis = now - 36 * MINUTE_MILLIS,
                        articleUrl = "https://www.danas.rs/vesti/ekonomija/",
                    ),
                ),
            ),
        )
    }

    private fun mockArticle(
        id: String,
        title: String,
        summary: String,
        source: String,
        category: NewsCategory,
        publishedAtEpochMillis: Long,
        articleUrl: String,
    ) = Article(
        id = id,
        title = title,
        summary = summary,
        source = source,
        category = category,
        publishedAtEpochMillis = publishedAtEpochMillis,
        imageUrl = null,
        articleUrl = articleUrl,
    )

    private companion object {
        const val MINUTE_MILLIS = 60_000L
    }
}
