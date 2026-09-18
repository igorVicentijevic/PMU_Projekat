package com.example.newsagreggator.util.comparator

import java.util.Locale
import javax.inject.Inject
import kotlin.math.max

class LevenshteinWordComparator @Inject constructor() : WordComparator {
    override fun areSame(first: String, second: String): Boolean {
        val normalizedFirst = first.trim().lowercase(Locale.ROOT)
        val normalizedSecond = second.trim().lowercase(Locale.ROOT)

        if (normalizedFirst == normalizedSecond) {
            return true
        }

        val longestLength = max(normalizedFirst.length, normalizedSecond.length)
        if (
            longestLength < MIN_FUZZY_WORD_LENGTH ||
            normalizedFirst.isEmpty() ||
            normalizedSecond.isEmpty()
        ) {
            return false
        }

        val distance = levenshteinDistance(normalizedFirst, normalizedSecond)
        val similarity = 1.0 - distance.toDouble() / longestLength
        return similarity >= MIN_SIMILARITY
    }

    private fun levenshteinDistance(first: String, second: String): Int {
        var previousRow = IntArray(second.length + 1) { it }
        var currentRow = IntArray(second.length + 1)

        first.forEachIndexed { firstIndex, firstCharacter ->
            currentRow[0] = firstIndex + 1

            second.forEachIndexed { secondIndex, secondCharacter ->
                val insertion = currentRow[secondIndex] + 1
                val deletion = previousRow[secondIndex + 1] + 1
                val substitution = previousRow[secondIndex] +
                    if (firstCharacter == secondCharacter) 0 else 1

                currentRow[secondIndex + 1] =
                    minOf(insertion, deletion, substitution)
            }

            val completedRow = previousRow
            previousRow = currentRow
            currentRow = completedRow
        }

        return previousRow[second.length]
    }

    companion object {
        const val MIN_SIMILARITY = 0.80
        const val MIN_FUZZY_WORD_LENGTH = 5
    }
}