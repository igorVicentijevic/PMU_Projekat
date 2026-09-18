package com.example.newsagreggator.business.service

import com.example.newsagreggator.business.model.Article

fun interface BreakingNewsStrategy {
    fun isBreaking(article: Article): Boolean
}
