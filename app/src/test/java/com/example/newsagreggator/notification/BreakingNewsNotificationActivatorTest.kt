package com.example.newsagreggator.notification

import com.example.newsagreggator.notification.AppNotification
import com.example.newsagreggator.model.Article
import com.example.newsagreggator.model.NewsCategory
import com.example.newsagreggator.repository.NewsRepository
import com.example.newsagreggator.repository.NewsSnapshot
import com.example.newsagreggator.repository.UserPreferences
import com.example.newsagreggator.repository.UserPreferencesRepository
import com.example.newsagreggator.notification.strategy.BreakingNewsDecision
import com.example.newsagreggator.notification.NotificationActivationPoint
import com.example.newsagreggator.notification.BreakingNewsNotificationActivator
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class BreakingNewsNotificationActivatorTest {
    @Test
    fun emitsNewArticleAcceptedByStrategy() = runBlocking {
        val existingArticle = article("existing")
        val breakingArticle = article("breaking")
        val news = MutableStateFlow(
            NewsSnapshot(
                articles = listOf(existingArticle),
                lastSuccessfulRefreshEpochMillis = 1L,
            )
        )
        val activatedNotification = CompletableDeferred<AppNotification>()
        val activator = BreakingNewsNotificationActivator(
            newsRepository = fakeNewsRepository(news),
            userPreferencesRepository = enabledPreferencesRepository(),
            breakingNewsStrategy = { article ->
                val isBreaking = article.id == breakingArticle.id
                BreakingNewsDecision(
                    isBreaking = isBreaking,
                    score = if (isBreaking) 100 else 0,
                    reasons = setOf("test"),
                )
            },
            notificationActivationPoint = object :
                NotificationActivationPoint {
                override fun notificationFlow():
                        Flow<AppNotification> = emptyFlow()

                override suspend fun activate(
                    notification: AppNotification,
                ) {
                    activatedNotification.complete(notification)
                }
            },
        )
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)

        activator.start(scope)
        assertFalse(activatedNotification.isCompleted)

        news.value = NewsSnapshot(
            articles = listOf(breakingArticle, existingArticle),
            lastSuccessfulRefreshEpochMillis = 2L,
        )

        assertEquals(
            breakingArticle.title,
            withTimeout(1_000L) { activatedNotification.await() }.title,
        )
        scope.cancel()
    }

    private fun fakeNewsRepository(
        news: Flow<NewsSnapshot>,
    ) = object : NewsRepository {
        override val news = news

        override suspend fun refreshArticles(): Result<Unit> =
            Result.success(Unit)
    }

    private fun enabledPreferencesRepository() =
        object : UserPreferencesRepository {
            override val preferences = MutableStateFlow(
                UserPreferences(breakingNewsEnabled = true)
            )

            override suspend fun setDarkThemeOverride(enabled: Boolean) = Unit

            override suspend fun setCompactLayout(enabled: Boolean) = Unit

            override suspend fun setRefreshInterval(minutes: Int) = Unit

            override suspend fun setBreakingNewsEnabled(enabled: Boolean) = Unit

            override suspend fun setFollowedCategories(
                categories: Set<Int>,
            ) = Unit
        }

    private fun article(id: String) = Article(
        id = id,
        title = "Article $id",
        summary = "Summary",
        source = "Test",
        category = NewsCategory.Serbia,
        publishedAtEpochMillis = 0L,
        imageUrl = null,
        articleUrl = "https://example.com/$id",
    )
}
