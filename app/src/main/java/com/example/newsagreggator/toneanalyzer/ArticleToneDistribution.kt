package com.example.newsagreggator.toneanalyzer

data class ArticleToneDistribution(
    val negative: Int,
    val neutral: Int,
    val positive: Int,
) {
    init {
        require(negative >= 0)
        require(neutral >= 0)
        require(positive >= 0)
        require(negative + neutral + positive == TOTAL_PERCENT)
    }

    companion object {
        val Sample = ArticleToneDistribution(
            negative = 20,
            neutral = 40,
            positive = 40,
        )

        private const val TOTAL_PERCENT = 100
    }
}
