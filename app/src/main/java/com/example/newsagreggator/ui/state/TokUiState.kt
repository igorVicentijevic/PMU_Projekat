package com.example.newsagreggator.ui.state

data class TokUiState(
    val compactLayout: Boolean = false,
    val savedArticleIds: Set<Int> = emptySet(),
    val readArticleIds: Set<Int> = emptySet(),
)
