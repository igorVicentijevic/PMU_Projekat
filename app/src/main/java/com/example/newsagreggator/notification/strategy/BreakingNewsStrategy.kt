package com.example.newsagreggator.notification.strategy

import com.example.newsagreggator.model.Article

data class BreakingNewsDecision(
    val isBreaking: Boolean,
    val score: Int,
    val reasons: Set<String>,
)

fun interface BreakingNewsStrategy {
    fun evaluate(article: Article): BreakingNewsDecision

    fun isBreaking(article: Article): Boolean = evaluate(article).isBreaking
}