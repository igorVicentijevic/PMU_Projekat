package com.example.newsagreggator.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.newsagreggator.data.NewsRepository

fun tokViewModelFactory(
    newsRepository: NewsRepository,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        TokViewModel(newsRepository)
    }
}
