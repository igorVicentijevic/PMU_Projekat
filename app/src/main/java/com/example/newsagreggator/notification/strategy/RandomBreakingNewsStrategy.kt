package com.example.newsagreggator.notification.strategy

import com.example.newsagreggator.model.Article
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