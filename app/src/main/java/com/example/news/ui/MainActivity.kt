package com.example.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.news.databinding.ActivityNewsBinding
import com.example.news.db.ArticleDatabase
import com.example.news.repository.NewsRepository
import com.example.news.ui.NewsViewModel
import com.example.news.ui.NewsViewModelProviderFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsActivity : AppCompatActivity() {


    lateinit var binding:ActivityNewsBinding

    private val newsViewModel:NewsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_news)
        val navHostFragment= supportFragmentManager.findFragmentByTag("navHostFragment")



        binding.bottomNavigationView.setupWithNavController(navHostFragment!!.findNavController())
    }
}