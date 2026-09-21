package com.example.newsagreggator.articlegrouping

import com.example.newsagreggator.model.Article
import com.example.newsagreggator.model.NewsCategory
import com.example.newsagreggator.repository.NewsRepository
import com.example.newsagreggator.repository.NewsSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleGroupingServiceTest {
    @Test
    fun groups_usesRepositoryArticlesAndDelegatesToStrategy() = runBlocking {
        val article = article()
        val repository = FakeNewsRepository(listOf(article))
        val expected = listOf(
            ArticleGroup(
                id = "group",
                title = "Synthetic title",
                articles = listOf(article),
            )
        )
        val service = ArticleGroupingService(
            newsRepository = repository,
            groupingStrategy = object : ArticleGroupingStrategy {
                override suspend fun group(
                    articles: List<Article>,
                ): List<ArticleGroup> {
                    assertEquals(listOf(article), articles)
                    return expected
                }
            }
        )

        assertEquals(expected, service.groups.first())
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

    private class FakeNewsRepository(
        articles: List<Article>,
    ) : NewsRepository {
        override val news = MutableStateFlow(
            NewsSnapshot(
                articles = articles,
                lastSuccessfulRefreshEpochMillis = null,
            )
        )

        override suspend fun refreshArticles(): Result<Unit> =
            Result.success(Unit)
    }
}
