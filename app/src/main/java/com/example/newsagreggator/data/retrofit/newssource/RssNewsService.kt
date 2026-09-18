package com.example.newsagreggator.data.retrofit.newssource

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface RssNewsService {
    @Headers("User-Agent: Tok Android News Reader")
    @GET
    suspend fun getFeed(@Url url: String): String
}
