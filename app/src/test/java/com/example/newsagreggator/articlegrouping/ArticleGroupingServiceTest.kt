package com.example.newsagreggator.articlegrouping

import com.example.newsagreggator.model.Article
import com.example.newsagreggator.model.NewsCategory
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleGroupingServiceTest {
    @Test
    fun getGroups_delegatesToConfiguredStrategy() = runBlocking {
        val article = article()
        val expected = listOf(
            ArticleGroup(
                id = "group",
                title = "Synthetic title",
                articles = listOf(article),
            )
        )
        val service = ArticleGroupingService(
            groupingStrategy = object : ArticleGroupingStrategy {
                override suspend fun group(
                    articles: List<Article>,
                ): List<ArticleGroup> {
                    assertEquals(listOf(article), articles)
                    return expected
                }
            }
        )

        assertEquals(expected, service.getGroups(listOf(article)))
    }

    private fun article() = Article(
        id = "article",
        title = "Title",
        summary = "Summary",
        source = "Source",
        category = NewsCategory.World,
        publishedAtEpochMillis = 0L,
        imageUrl = null,
        articleUrl = "https://example.com/article",
    )
}
