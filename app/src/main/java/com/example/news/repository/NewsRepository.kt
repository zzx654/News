package com.example.news.repository

import android.annotation.SuppressLint
import com.example.news.api.RetrofitInstance
import com.example.news.db.ArticleDatabase
import com.example.news.models.Article
import com.example.news.models.NewsResponse
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(private val db:ArticleDatabase) {

        fun getBreakingNews(countryCode:String,pageNumber:Int): Single<Response<NewsResponse>>{
           return RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)
        }

        fun searchNews(searchQuery:String,pageNumber:Int):Single<Response<NewsResponse>>{
            return RetrofitInstance.api.searchForNews(searchQuery,pageNumber)
        }

        @SuppressLint("CheckResult")
        fun upsert(article: Article){
            Observable.just(article)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            {article->

                                db.getArticleDao().upsert(article)
                            },
                            {

                            })
        }

        fun getSavedNEws()=db.getArticleDao().getAllArticles()

        @SuppressLint("CheckResult")
        fun deleteArticle(article: Article){
            Observable.just(article)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            {article->
                                db.getArticleDao().deleteArticle(article)

                            },
                            {

                            }
                    )

        }


}