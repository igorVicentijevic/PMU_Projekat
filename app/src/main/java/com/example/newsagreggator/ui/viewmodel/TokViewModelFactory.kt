package com.example.newsagreggator.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.newsagreggator.data.NewsRepository
import com.example.newsagreggator.data.local.repository.ArticleStateRepository
import com.example.newsagreggator.data.network.NetworkMonitor
import com.example.newsagreggator.data.preferences.UserPreferencesRepository

fun tokViewModelFactory(
    newsRepository: NewsRepository,
    userPreferencesRepository: UserPreferencesRepository,
    articleStateRepository: ArticleStateRepository,
    networkMonitor: NetworkMonitor,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        TokViewModel(
            newsRepository = newsRepository,
            userPreferencesRepository = userPreferencesRepository,
            articleStateRepository = articleStateRepository,
            networkMonitor = networkMonitor,
        )
    }
}
