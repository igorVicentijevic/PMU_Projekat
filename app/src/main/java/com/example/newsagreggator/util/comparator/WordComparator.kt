package com.example.newsagreggator.util.comparator

fun interface WordComparator {
    fun areSame(first: String, second: String): Boolean
}