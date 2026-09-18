package com.example.newsagreggator.data.notification

import com.example.newsagreggator.business.model.AppNotification
import com.example.newsagreggator.business.repository.NewsRepository
import com.example.newsagreggator.business.repository.UserPreferencesRepository
import com.example.newsagreggator.business.service.BreakingNewsStrategy
import com.example.newsagreggator.business.service.NotificationActivationPoint
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Singleton
class BreakingNewsNotificationActivator @Inject constructor(
    private val newsRepository: NewsRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val breakingNewsStrategy: BreakingNewsStrategy,
    private val notificationActivationPoint: NotificationActivationPoint,
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
                    //filtering articles that are not duplicates and that are not breaking news
                    if (
                        article.id !in previousArticleIds &&
                        breakingNewsStrategy.isBreaking(article)
                    ) {
                        //emitting notification for breaking news
                        notificationActivationPoint.activate(
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
