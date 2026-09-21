package com.example.newsagreggator.articlegrouping

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockArticleGroupingStrategyTest {
    private val strategy = MockArticleGroupingStrategy()

    @Test
    fun group_returnsFixedEventClusters() = runBlocking {
        val groups = strategy.group(emptyList())

        assertEquals(3, groups.size)
        assertEquals(
            listOf(
                "Otvaranje novog parka uz Savu",
                "Evropski plan za prelazak na čistu energiju",
                "Domaći tim predstavio novi AI alat",
            ),
            groups.map(ArticleGroup::title),
        )
    }

    @Test
    fun eachGroupContainsCoverageFromDifferentNewsSources() = runBlocking {
        val groups = strategy.group(emptyList())

        groups.forEach { group ->
            assertTrue(group.articles.size >= 3)
            assertEquals(
                group.articles.size,
                group.articles.map { it.source }.toSet().size,
            )
        }
    }

    @Test
    fun articlesWithinEachMockGroupDescribeTheSameEvent() = runBlocking {
        val groups = strategy.group(emptyList())

        assertTrue(
            groups[0].articles.all { article ->
                "savu" in article.title.lowercase() &&
                    (
                        "park" in article.title.lowercase() ||
                            "zelenu oazu" in article.title.lowercase()
                        )
            }
        )
        assertTrue(
            groups[1].articles.all { article ->
                val text = "${article.title} ${article.summary}".lowercase()
                text.contains("energ") || text.contains("obnovljiv")
            }
        )
        assertTrue(
            groups[2].articles.all { article ->
                val text = "${article.title} ${article.summary}".lowercase()
                text.contains("ai") || text.contains("inteligencij")
            }
        )
    }
}
