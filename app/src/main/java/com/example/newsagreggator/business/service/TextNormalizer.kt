package com.example.newsagreggator.business.service

fun interface TextNormalizer {
    fun normalize(text: String): String
}
