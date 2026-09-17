package com.example.newsagreggator.data.remote.source

import com.example.newsagreggator.data.remote.parser.RssImageUrlNormalizers
import com.example.newsagreggator.domain.model.NewsCategory

object NewsSources {
    val all: List<NewsSource> = listOf(
        NewsSource(
            id = "rts",
            displayName = "RTS",
            feeds = listOf(
                NewsFeed("https://www.rts.rs/vesti/rss.html"),
                NewsFeed(
                    "https://www.rts.rs/vesti/srbija-danas/rss.html",
                    NewsCategory.Serbia,
                ),
                NewsFeed(
                    "https://www.rts.rs/vesti/svet/rss.html",
                    NewsCategory.World,
                ),
                NewsFeed(
                    "https://www.rts.rs/vesti/ekonomija/rss.html",
                    NewsCategory.Business,
                ),
                NewsFeed(
                    "https://www.rts.rs/magazin/kultura/rss.html",
                    NewsCategory.Culture,
                ),
                NewsFeed(
                    "https://www.rts.rs/sport/rss.html",
                    NewsCategory.Sport,
                ),
                NewsFeed(
                    "https://www.rts.rs/magazin/tehnologija/rss.html",
                    NewsCategory.Technology,
                ),
                NewsFeed(
                    "https://www.rts.rs/magazin/zdravlje/rss.html",
                    NewsCategory.Health,
                ),
            ),
            imageUrlNormalizer = RssImageUrlNormalizers::normalizeRts,
        ),
        NewsSource(
            id = "danas",
            displayName = "Danas",
            feeds = listOf(NewsFeed("https://www.danas.rs/feed/")),
        ),
        NewsSource(
            id = "n1",
            displayName = "N1",
            feeds = listOf(NewsFeed("https://n1info.rs/feed/")),
        ),
        NewsSource(
            id = "nova",
            displayName = "Nova",
            feeds = listOf(NewsFeed("https://nova.rs/feed/")),
        ),
        NewsSource(
            id = "euronews-serbia",
            displayName = "Euronews Srbija",
            feeds = listOf(
                NewsFeed(
                    "https://www.euronews.rs/rss/srbija",
                    NewsCategory.Serbia,
                ),
                NewsFeed(
                    "https://www.euronews.rs/rss/svet",
                    NewsCategory.World,
                ),
                NewsFeed(
                    "https://www.euronews.rs/rss/biznis",
                    NewsCategory.Business,
                ),
                NewsFeed(
                    "https://www.euronews.rs/rss/kultura",
                    NewsCategory.Culture,
                ),
                NewsFeed(
                    "https://www.euronews.rs/rss/sport",
                    NewsCategory.Sport,
                ),
                NewsFeed(
                    "https://www.euronews.rs/rss/magazin/tehnologija",
                    NewsCategory.Technology,
                ),
                NewsFeed(
                    "https://www.euronews.rs/rss/magazin/zdravlje",
                    NewsCategory.Health,
                ),
            ),
        ),
    )
}
