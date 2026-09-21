package com.example.newsagreggator.articlegrouping

import com.example.newsagreggator.repository.NewsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArticleGroupingService @Inject constructor(
    newsRepository: NewsRepository,
    private val groupingStrategy: ArticleGroupingStrategy,
) {
    val groups: Flow<List<ArticleGroup>> =
        newsRepository.news.map { newsSnapshot ->
            groupingStrategy.group(newsSnapshot.articles)
        }
}
