package com.example.newsagreggator.data.remote

import retrofit2.http.GET
import retrofit2.http.Headers

interface RtsNewsService {
    @Headers("User-Agent: Tok Android News Reader")
    @GET("vesti/rss.html")
    suspend fun getNewsFeed(): String

    @Headers("User-Agent: Tok Android News Reader")
    @GET("sport/rss.html")
    suspend fun getSportsFeed(): String
}
