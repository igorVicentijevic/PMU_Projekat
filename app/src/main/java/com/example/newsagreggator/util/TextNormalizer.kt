package com.example.newsagreggator.util

fun interface TextNormalizer {
    fun normalize(text: String): String
}