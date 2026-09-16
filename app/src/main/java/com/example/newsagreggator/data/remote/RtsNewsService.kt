package com.example.newsagreggator.data.remote

import retrofit2.http.GET
import retrofit2.http.Headers

interface RtsNewsService {
    @Headers("User-Agent: Tok Android News Reader")
    @GET("vesti/rss.html")
    suspend fun getNewsFeed(): String

    @Headers("User-Agent: Tok Android News Reader")
    @GET("vesti/srbija-danas/rss.html")
    suspend fun getSerbiaFeed(): String

    @Headers("User-Agent: Tok Android News Reader")
    @GET("vesti/svet/rss.html")
    suspend fun getWorldFeed(): String

    @Headers("User-Agent: Tok Android News Reader")
    @GET("vesti/ekonomija/rss.html")
    suspend fun getBusinessFeed(): String

    @Headers("User-Agent: Tok Android News Reader")
    @GET("magazin/kultura/rss.html")
    suspend fun getCultureFeed(): String

    @Headers("User-Agent: Tok Android News Reader")
    @GET("sport/rss.html")
    suspend fun getSportsFeed(): String

    @Headers("User-Agent: Tok Android News Reader")
    @GET("magazin/tehnologija/rss.html")
    suspend fun getTechnologyFeed(): String

    @Headers("User-Agent: Tok Android News Reader")
    @GET("magazin/zdravlje/rss.html")
    suspend fun getHealthFeed(): String
}
