package com.example.newsagreggator.data.remote.parser

object RssImageUrlNormalizers {
    fun normalizeRts(url: String): String {
        val secureUrl = url.replace(
            regex = Regex(
                pattern = "^http://(?:www\\.)?rts\\.rs/",
                option = RegexOption.IGNORE_CASE,
            ),
            replacement = "https://www.rts.rs/",
        )
        val normalizedPath = secureUrl.replace(
            oldValue = "/upload/thumbnail//",
            newValue = "/upload//",
        )
        val lastPathSeparator = normalizedPath.lastIndexOf('/')
        if (lastPathSeparator <= 0) return normalizedPath

        val fileName = normalizedPath.substring(lastPathSeparator + 1)
        val parentPath = normalizedPath.substring(0, lastPathSeparator)
        return if (parentPath.endsWith("/$fileName")) {
            parentPath
        } else {
            normalizedPath
        }
    }
}
