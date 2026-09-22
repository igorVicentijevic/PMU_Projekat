package com.example.newsagreggator.notification

import com.example.newsagreggator.notification.strategy.BreakingNewsStrategy
import com.example.newsagreggator.repository.NewsRepository
import com.example.newsagreggator.repository.UserPreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Singleton
class BreakingNewsNotificationActivator @Inject constructor(
    private val newsRepository: NewsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val breakingNewsStrategy: BreakingNewsStrategy,
    private val notificationPublisher: NotificationPublisher,
) {
    private var started = false

    @Synchronized
    fun start(scope: CoroutineScope) {
        if (started) {
            return
        }
        started = true

        scope.launch {
            var knownArticleIds: Set<String>? = null

            newsRepository.news.collect { snapshot ->
                //possible there will be duplicates in current snapshot
                val previousArticleIds = knownArticleIds

                knownArticleIds = snapshot.articles.mapTo(mutableSetOf()) {
                    it.id
                }

                if (
                    previousArticleIds == null ||
                    !userPreferencesRepository.preferences
                        .first()
                        .breakingNewsEnabled
                ) {
                    return@collect
                }

                snapshot.articles.forEach { article ->
                    //Only new articles accepted by the strategy are published.
                    if (
                        article.id !in previousArticleIds &&
                        breakingNewsStrategy.isBreaking(article)
                    ) {
                        notificationPublisher.publish(
                            AppNotification(
                                id = article.id.hashCode(),
                                title = article.title,
                                message = article.summary,
                                destinationUrl = article.articleUrl,
                            )
                        )
                    }
                }
            }
        }
    }
}
