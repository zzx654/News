package com.example.news.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.example.news.R
import com.example.news.databinding.FragmentArticleBinding
import com.example.news.models.Article
import com.example.news.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment: Fragment(R.layout.fragment_article) {

    lateinit var newsViewModel: NewsViewModel

    lateinit var binding:FragmentArticleBinding


    val args:ArticleFragmentArgs by navArgs()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DataBindingUtil.inflate<FragmentArticleBinding>(inflater,R.layout.fragment_article,container,false)
        newsViewModel=ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)

        val article=args.article
       binding.webView.apply{
            webViewClient= WebViewClient()
            loadUrl(article.url)
        }
        binding.fab.setOnClickListener{
            newsViewModel.saveArticle(article)
            Snackbar.make(binding.root,"Article saved successfully", Snackbar.LENGTH_SHORT).show()//잘안된다면 이것이 문제
        }

        return binding.root
    }
}