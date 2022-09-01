package com.example.news.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.models.Article
import com.example.news.models.NewsResponse
import com.example.news.repository.NewsRepository
import com.example.news.util.Resource
import com.example.news.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(val newsRepository: NewsRepository): ViewModel()
{
    private var compositeDisposable=CompositeDisposable()

    val breakingNews:MutableLiveData<Resource<NewsResponse>> =MutableLiveData()
    var breakingNewsPage=0

    var breakingNewsResponse:NewsResponse?=null

    val searchNews:MutableLiveData<Resource<NewsResponse>> =MutableLiveData()
    var searchNewsPage=0
    var searchNewsResponse:NewsResponse?=null




    init{
        getBreakingNews("us")
    }

    fun getBreakingNews(countryCode:String)
    {
        breakingNews.postValue(Resource.Loading<NewsResponse>())

        compositeDisposable.add(
            newsRepository.getBreakingNews(countryCode,breakingNewsPage+1)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {resultdata->
                        handleBreakingNewsResponse(resultdata)

                    }
                ,
                    {
                        Log.e("속보에러메시지",it.message!!)
                        breakingNews.postValue(Resource.Error<NewsResponse>("Check the internet connection"))
                    }
                )
        )
        //val response=newsRepository.getBreakingNews(countryCode,breakingNewsPage)
        //breakingNews.postValue(handleBreakingNewsResponse(response))

    }

    fun searchNews(searchQuery:String,isnewSearch:Boolean){
        searchNews.postValue(Resource.Loading<NewsResponse>())
        if(isnewSearch) {
            searchNewsPage = 0
        }

        compositeDisposable.add(
            newsRepository.searchNews(searchQuery,searchNewsPage+1)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {resultdata->
                        handleSearchNewsResponse(resultdata,isnewSearch)

                    },
                    {
                        Log.e("서치에러메시지",it.message!!)
                        searchNews.postValue(Resource.Error<NewsResponse>("Check the internet connection"))
                   }
                )
        )
    }

    private fun handleBreakingNewsResponse(response:Response<NewsResponse>){

            response.body()?.let{resultResponse->
                breakingNewsPage++
                if(breakingNewsResponse==null){//b reakingNewsResponse가 null일때(맨처음에)
                    breakingNewsResponse=resultResponse//response의 값을 할당
                }
                else{//두번째부터
                    val oldArticles=breakingNewsResponse?.articles//breakindNewsResponse가 null이 아니라면 articles를 oldAticle에 할당
                    val newArticles=resultResponse.articles
                    oldArticles?.addAll(newArticles)
                    print("BREAKINGNEWSRESPONSE")
                   // println(breakingNewsResponse?.articles)
                   // print("oldArticles")
                   // println(oldArticles)
                    println()
                }
                breakingNews.postValue(Resource.Success<NewsResponse>(breakingNewsResponse?:resultResponse))//breakingNews가 null이면 resultResponse를 인자로 함


            }
    }

    private fun handleSearchNewsResponse(response:Response<NewsResponse>,isnewSearch: Boolean){

            response.body()?.let{resultResponse->
                if(isnewSearch){//새로운 검색일경우

                    searchNewsResponse=null
                }
                searchNewsPage++
                if(searchNewsResponse==null){
                    searchNewsResponse=resultResponse
                }
                else{
                    val oldArticles=searchNewsResponse?.articles
                    val newArticles=resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }

                searchNews.postValue(Resource.Success<NewsResponse>(searchNewsResponse?:resultResponse))
            }



    }


    fun saveArticle(article: Article){
        newsRepository.upsert(article)
    }

    fun getSavedNews()=newsRepository.getSavedNEws()

    fun deleteArticle(article:Article){
        newsRepository.deleteArticle(article)
    }




    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}