package com.example.newsagreggator.util

import com.example.newsagreggator.util.comparator.LevenshteinWordComparator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LevenshteinWordComparatorTest {
    private val comparator = LevenshteinWordComparator()

    @Test
    fun acceptsExactWordsIgnoringCase() {
        assertTrue(comparator.areSame("ZEMLJOTRES", "zemljotres"))
    }

    @Test
    fun acceptsLongWordsAtEightyPercentSimilarity() {
        assertTrue(comparator.areSame("zemljotresu", "zemljotres"))
        assertTrue(comparator.areSame("eksplozja", "eksplozija"))
    }

    @Test
    fun rejectsWordsBelowEightyPercentSimilarity() {
        assertFalse(comparator.areSame("zemlja", "zemljotres"))
    }

    @Test
    fun requiresExactMatchForShortWords() {
        assertFalse(comparator.areSame("rat", "rad"))
    }
}
