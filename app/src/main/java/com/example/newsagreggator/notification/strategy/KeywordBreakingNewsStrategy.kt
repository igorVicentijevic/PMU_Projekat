package com.example.newsagreggator.notification.strategy

import com.example.newsagreggator.model.Article
import com.example.newsagreggator.util.TextNormalizer
import com.example.newsagreggator.util.comparator.WordComparator
import javax.inject.Inject

class KeywordBreakingNewsStrategy internal constructor(
    private val textNormalizer: TextNormalizer,
    private val wordComparator: WordComparator,
    private val currentTimeMillis: () -> Long,
) : BreakingNewsStrategy {
    @Inject
    constructor(
        textNormalizer: TextNormalizer,
        wordComparator: WordComparator,
    ) : this(
        textNormalizer = textNormalizer,
        wordComparator = wordComparator,
        currentTimeMillis = System::currentTimeMillis,
    )

    //checks if the word is in set to compare and add appropriate reason and score
    private fun checkAndGetScore(words: Set<String>,  wordsToCompare:  Set<String>, reason: String,scoreToAdd:Int, reasons: MutableSet<String>): Int{

        if (words.containsAny(wordsToCompare)) {

            reasons += reason
            return scoreToAdd
        }
        return 0


    }

    private fun hasStrongReason(reasons: Set<String>): Boolean{
        return REASON_URGENCY_TITLE in reasons ||
                REASON_URGENCY_SUMMARY in reasons ||
                REASON_HIGH_IMPACT in reasons
    }

    override fun evaluate(article: Article): BreakingNewsDecision {
        var score = 0
        val reasons = mutableSetOf<String>()
        val titleWords = words(article.title)
        val summaryWords = words(article.summary)
        val allWords = titleWords + summaryWords

        score += checkAndGetScore(titleWords, BreakingNewsVocabulary.urgencyWords, REASON_URGENCY_TITLE,URGENCY_TITLE_SCORE,reasons)

        score += checkAndGetScore(summaryWords, BreakingNewsVocabulary.urgencyWords,REASON_URGENCY_SUMMARY,URGENCY_SUMMARY_SCORE,reasons)

        score += checkAndGetScore(allWords, BreakingNewsVocabulary.highImpactWords,REASON_HIGH_IMPACT,HIGH_IMPACT_SCORE,reasons)

        score += checkAndGetScore(allWords, BreakingNewsVocabulary.escalationWords,REASON_ESCALATION,ESCALATION_SCORE,reasons)

        score += checkAndGetScore(allWords, BreakingNewsVocabulary.analysisWords,REASON_ANALYSIS,ANALYSIS_PENALTY,reasons)

        score += freshnessScore(article, reasons)

        if (startsWithUppercaseUrgency(article.title, titleWords)) {
            score += UPPERCASE_URGENCY_SCORE
            reasons += REASON_UPPERCASE_URGENCY
        }

        val hasStrongSignal = hasStrongReason(reasons)

        return BreakingNewsDecision(
            isBreaking = score >= BREAKING_THRESHOLD && hasStrongSignal,
            score = score,
            reasons = reasons,
        )
    }

    private fun words(text: String): Set<String> =
        textNormalizer.normalize(text)
            .split(' ')
            .filterTo(mutableSetOf(), String::isNotBlank)

    private fun Set<String>.containsAny(keywords: Set<String>): Boolean =
        any { word ->
            keywords.any { keyword ->
                wordComparator.areSame(word, keyword)
            }
        }

    //how relevante is article
    private fun freshnessScore(
        article: Article,
        reasons: MutableSet<String>,
    ): Int {

        val ageMillis =
            (currentTimeMillis() - article.publishedAtEpochMillis).coerceAtLeast(0)

        val ageMinutes = ageMillis / MILLIS_PER_MINUTE

        return when {
            ageMinutes <= VERY_FRESH_MINUTES -> {
                reasons += REASON_VERY_FRESH
                VERY_FRESH_SCORE
            }

            ageMinutes <= FRESH_MINUTES -> {
                reasons += REASON_FRESH
                FRESH_SCORE
            }

            ageMinutes > STALE_MINUTES -> {
                reasons += REASON_STALE
                STALE_PENALTY
            }

            else -> 0
        }
    }

    private fun startsWithUppercaseUrgency(
        originalTitle: String,
        normalizedTitleWords: Set<String>,
    ): Boolean {
        val firstOriginalWord = originalTitle
            .trim()
            .substringBefore(' ')
            .filter(Char::isLetter)

        if (
            firstOriginalWord.isEmpty() ||
            firstOriginalWord != firstOriginalWord.uppercase()
        ) {
            return false
        }

        val firstNormalizedWord = textNormalizer.normalize(firstOriginalWord)
        return normalizedTitleWords.contains(firstNormalizedWord) &&
            BreakingNewsVocabulary.urgencyWords.any { keyword ->
                wordComparator.areSame(firstNormalizedWord, keyword)
            }
    }

    private companion object {
        const val BREAKING_THRESHOLD = 50
        const val URGENCY_TITLE_SCORE = 40
        const val URGENCY_SUMMARY_SCORE = 20
        const val HIGH_IMPACT_SCORE = 30
        const val ESCALATION_SCORE = 15
        const val VERY_FRESH_SCORE = 15
        const val FRESH_SCORE = 5
        const val UPPERCASE_URGENCY_SCORE = 5
        const val ANALYSIS_PENALTY = -25
        const val STALE_PENALTY = -30

        const val VERY_FRESH_MINUTES = 30
        const val FRESH_MINUTES = 90
        const val STALE_MINUTES = 120
        const val MILLIS_PER_MINUTE = 60_000L

        const val REASON_URGENCY_TITLE = "urgency-in-title"
        const val REASON_URGENCY_SUMMARY = "urgency-in-summary"
        const val REASON_HIGH_IMPACT = "high-impact-event"
        const val REASON_ESCALATION = "escalation"
        const val REASON_VERY_FRESH = "published-within-30-minutes"
        const val REASON_FRESH = "published-within-90-minutes"
        const val REASON_UPPERCASE_URGENCY = "uppercase-urgency-prefix"
        const val REASON_ANALYSIS = "analysis-or-opinion"
        const val REASON_STALE = "older-than-two-hours"

    }
}