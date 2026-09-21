package com.example.newsagreggator.toneanalyzer

import com.example.newsagreggator.model.Article
import javax.inject.Inject
import kotlin.random.Random

class SampleArticleToneAnalysisStrategy @Inject constructor() :
    ArticleToneAnalysisStrategy {

    override suspend fun analyze(
        article: Article,
    ): ArticleToneDistribution {
        val analyzableText = "${article.title}\n\n${article.summary}"
        val random = Random(analyzableText.hashCode())
        val firstCut = random.nextInt(from = 10, until = 61)
        val secondCut = random.nextInt(
            from = firstCut + 10,
            until = 91,
        )

        return ArticleToneDistribution(
            negative = firstCut,
            neutral = secondCut - firstCut,
            positive = 100 - secondCut,
        )
    }
}
