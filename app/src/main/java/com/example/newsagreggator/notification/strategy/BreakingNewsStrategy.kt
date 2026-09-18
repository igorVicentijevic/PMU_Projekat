package com.example.newsagreggator.notification.strategy

import com.example.newsagreggator.model.Article

fun interface BreakingNewsStrategy {
    fun isBreaking(article: Article): Boolean
}