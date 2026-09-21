package com.example.newsagreggator.pdf.util

internal object PdfPagePlanner {
    fun planLineRanges(
        lineHeights: List<Int>,
        firstPageAvailableHeight: Int,
        continuationPageAvailableHeight: Int,
    ): List<IntRange> {
        require(lineHeights.all { it > 0 })
        require(firstPageAvailableHeight > 0)
        require(continuationPageAvailableHeight > 0)
        if (lineHeights.isEmpty()) return emptyList()

        val ranges = mutableListOf<IntRange>()
        var firstLine = 0
        var availableHeight = firstPageAvailableHeight

        while (firstLine < lineHeights.size) {
            var consumedHeight = 0
            var nextLine = firstLine
            while (
                nextLine < lineHeights.size &&
                consumedHeight + lineHeights[nextLine] <= availableHeight
            ) {
                consumedHeight += lineHeights[nextLine]
                nextLine += 1
            }
            if (nextLine == firstLine) {
                nextLine += 1
            }

            ranges += firstLine until nextLine
            firstLine = nextLine
            availableHeight = continuationPageAvailableHeight
        }

        return ranges
    }
}