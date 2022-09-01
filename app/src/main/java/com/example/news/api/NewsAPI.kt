package com.example.news.api

import com.example.news.models.NewsResponse
import com.example.news.util.Constants.Companion.API_KEY
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    fun getBreakingNews(
        @Query("country")
        countrycode:String="us",
        @Query("page")
        pageNumber:Int=1,
        @Query("apiKey")
        apiKey:String=API_KEY
    ): Single<Response<NewsResponse>>

    @GET("v2/everyThing")
     fun searchForNews(
        @Query("q")
        searchQuery:String="us",
        @Query("page")
        pageNumber:Int=1,
        @Query("apiKey")
        apiKey:String=API_KEY
    ):Single<Response<NewsResponse>>
}