package com.example.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.adapter.NewsAdapter
import com.example.news.databinding.FragmentSavedNewsBinding
import com.example.news.databinding.FragmentSearchNewsBinding
import com.example.news.ui.NewsViewModel
import com.example.news.util.Constants
import com.example.news.util.Constants.Companion.QUERY_PAGE_SIZE
import com.example.news.util.Constants.Companion.SEARCH_NEWS_TIME_DELAY
import com.example.news.util.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment: Fragment(R.layout.fragment_search_news) {

    private lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter:NewsAdapter
    val TAG="SearchNewsFragment"
    lateinit var binding: FragmentSearchNewsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding= DataBindingUtil.inflate<FragmentSearchNewsBinding>(inflater,R.layout.fragment_search_news,container,false)
        newsViewModel= ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply{
                putSerializable("article",it)
            }
            findNavController().navigate(
                    R.id.action_searchNewsFragment_to_articleFragment,
                    bundle
            )
        }
        var job: Job?=null
        binding.etSearch.addTextChangedListener {editable->
            job?.cancel()
            job= MainScope().launch{
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let{
                    if(editable.toString().isNotEmpty()){
                        newsViewModel.searchNews(editable.toString(),true)
                    }
                }

            }

        }
        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer{response->
            when(response.status){
                Status.SUCCESS->{
                    hideProgressBar()
                    response.data?.let{newsResponse->
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        var totalPages=newsResponse.totalResults/ Constants.QUERY_PAGE_SIZE
                        if(newsResponse.totalResults%QUERY_PAGE_SIZE>0){
                            totalPages++
                        }
                        isLastPage= newsViewModel.searchNewsPage==totalPages
                        if(isLastPage){
                            binding.rvSearchNews.setPadding(0,0,0,0)
                        }

                    }
                }
                Status.ERROR->{
                    //hideProgressBar()
                    response.message?.let{message->
                        Log.e(TAG,"An error occured $message")
                        Toast.makeText(activity,message, Toast.LENGTH_LONG).show()

                    }
                }
                Status.LOADING->{
                    //showProgressBar()
                }

            }

            if(!newsAdapter.differ.currentList.isEmpty())
            {
                newsAdapter.setNetworkState(response.status)
            }

        })
        return binding.root
    }

    private fun hideProgressBar(){
        binding.progressVis=false
        isLoading=false
    }

    private fun showProgressBar(){
        binding.progressVis=true
        isLoading=true
    }


    var isLoading=false
    var isLastPage=false
    var isScrolling=false

    val scrollListener = object: RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager=recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition=layoutManager.findFirstVisibleItemPosition()//화면에 보여지는 가장 위 아이템의 포지션값
            val visibleItemCount=layoutManager.childCount//화면에 보여지는 아이템 갯수
            val totalItemCount=layoutManager.itemCount//화면을 무시한 전체 아이템의 수

            val isNotLoadingANdNotLastPage=!isLoading&&!isLastPage
            val isAtLastItem=firstVisibleItemPosition+visibleItemCount>=totalItemCount//화면에 보여지는 맨위 아이템의 포지션+화면에 보여지는 아이템의 수가 전체아이템보다 같거나 큰가
            val isNotAtBeginning=firstVisibleItemPosition>=0//화면에 보여지는 가장위 아이템 포지션값이 0보다 크거나 같은가
            val isTotalMoreThanVisible=totalItemCount>= Constants.QUERY_PAGE_SIZE//한번불러오는 페이지의 크기보다 전체 아이템갯수가 많은가(한번이상 불러왔는가)
            val shouldPaginate=isNotLoadingANdNotLastPage&&isAtLastItem&&isNotAtBeginning&&isTotalMoreThanVisible&&isScrolling

            if(shouldPaginate){

                newsViewModel.searchNews(binding.etSearch.text.toString(),false)
                isScrolling=false

            }
        }
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                isScrolling=true
        }


    }

    private fun setUpRecyclerView(){
        newsAdapter= NewsAdapter()
        binding.rvSearchNews.apply{
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }
}