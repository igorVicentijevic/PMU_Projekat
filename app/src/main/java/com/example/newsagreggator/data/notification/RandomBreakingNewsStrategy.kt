package com.example.newsagreggator.data.notification

import com.example.newsagreggator.business.model.Article
import com.example.newsagreggator.business.service.BreakingNewsStrategy
import javax.inject.Inject
import kotlin.random.Random

class RandomBreakingNewsStrategy internal constructor(
    private val random: Random,
) : BreakingNewsStrategy {
    @Inject
    constructor() : this(Random.Default)

    override fun isBreaking(article: Article): Boolean =
        random.nextBoolean()
}
