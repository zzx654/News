package com.example.news.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.adapter.NewsAdapter
import com.example.news.databinding.FragentBreakingNewsBinding
import com.example.news.databinding.FragmentSavedNewsBinding
import com.example.news.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {

    private lateinit var newsViewModel: NewsViewModel

    lateinit var newsAdapter:NewsAdapter
    lateinit var binding: FragmentSavedNewsBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding= DataBindingUtil.inflate<FragmentSavedNewsBinding>(inflater,R.layout.fragment_saved_news,container,false)
        newsViewModel= ViewModelProvider(requireActivity()).get(NewsViewModel::class.java)
        setUpRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply{
                putSerializable("article",it)
            }
            findNavController().navigate(
                    R.id.action_savedNewsFragment_to_articleFragment,
                    bundle
            )
        }

        val itemTouchHelperCallback=object:ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition//스와이프된 포지션가져옴
                val article=newsAdapter.differ.currentList[position]//어댑터 현재리스트에 포지션에 해당하는 article취함
                newsViewModel.deleteArticle(article)//article삭제
                Snackbar.make(binding.root,"Succesfully deleted article",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsViewModel.saveArticle(article)//스낵바의 undo버튼을 누르면 saveArticle을 함(다시 복구)

                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply{
            attachToRecyclerView(binding.rvSavedNews)
        }
        newsViewModel.getSavedNews().observe(viewLifecycleOwner, Observer {articles->
            newsAdapter.differ.submitList(articles)

        })
        return binding.root
    }

    private fun setUpRecyclerView(){
        newsAdapter= NewsAdapter()
        binding.rvSavedNews.apply{
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }

}