package com.example.news.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.R
import com.example.news.databinding.ItemArticlePreviewBinding
import com.example.news.databinding.NetworkStateItemBinding
import com.example.news.models.Article
import com.example.news.util.Status

 class NewsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ARTICLE_VIEW_TYPE=1
    val NETWORK_VIEW_TYPE=2

    private var loadingState: Status?=null
    inner class ArticleViewHolder(val binding:ItemArticlePreviewBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(article:Article)
        {
            binding.article=article
        }
    }

     inner class NetworkStateItemViewHolder(val binding: NetworkStateItemBinding):RecyclerView.ViewHolder(binding.root){//inflate된 뷰를 인자로

         fun bind(loadingState: Status?){
             if(loadingState!=null && loadingState==Status.LOADING){//로딩중일시
                 binding.progressVis= java.lang.Boolean.TRUE
             }
             else{
                 binding.progressVis= java.lang.Boolean.FALSE
             }


         }
     }

    private val differCallback=object: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url==newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }

    }
     private fun hasExtraRow():Boolean{
         return loadingState!=null && loadingState!= Status.SUCCESS//loadingState가 loading또는 에러인지
     }
     fun setNetworkState(newloadingState: Status?){
         val previousState=this.loadingState//이전 networkState
         val hadExtraRow=hasExtraRow()//새로운 아이템불러오는지(이전)
         this.loadingState=newloadingState//새 networkState
         val hasExtraRow=hasExtraRow()//새로운 아이템불러오는지(현재)

         if(hadExtraRow!=hasExtraRow){
             if(hadExtraRow){//이전에 새로운아이템 불러오는 상태이지만 현재는 불러오는 상태가 아니라면(loading->loaded)
                 notifyItemRemoved(differ.currentList.size)//프로그레스바 삭제
             }else{//이전에 새로운아이템 불러오는 상태가 아니지만 현재는 불러오는상태라면(null->loading)
                 notifyItemInserted(differ.currentList.size)//프로그레스바 추가
             }
         }else if(hasExtraRow&&previousState!=newloadingState){//hadextrarow,hasextrarow가 같으면서 networkstate가 이전에 비해 변한경우(loading->error)
             notifyItemChanged(itemCount-1)
             //System.out.println(super.getItemCount())
             //System.out.println(itemCount)
         }

     }


     override fun getItemViewType(position: Int): Int {
         return if(hasExtraRow()&&position==itemCount-1){//loading중이고 현 position이 전체 itemcount-1(인덱스 마지막)
             NETWORK_VIEW_TYPE
         }else{
             ARTICLE_VIEW_TYPE
         }
     }
    val differ=AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == ARTICLE_VIEW_TYPE) {
            return DataBindingUtil.inflate<ItemArticlePreviewBinding>(layoutInflater, R.layout.item_article_preview, parent, false).let {
                ArticleViewHolder(it)

            }

        } else {
            return DataBindingUtil.inflate<NetworkStateItemBinding>(layoutInflater, R.layout.network_state_item, parent, false).let {
                NetworkStateItemViewHolder(it)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(getItemViewType(position)==ARTICLE_VIEW_TYPE)
        {
            val article=differ.currentList[position]
            (holder as ArticleViewHolder).bind(article)
            holder.binding.root.setOnClickListener {
                onItemClickListener?.let{
                    it(article)
                }
            }
        }
        else{
            (holder as NetworkStateItemViewHolder).bind(loadingState)
        }
    }

    private var onItemClickListener: ((Article) -> Unit)?=null

    fun setOnItemClickListener(listener:(Article)->Unit){
        onItemClickListener=listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size+if(hasExtraRow())1 else 0
    }

    object BindingConversions {
        @BindingAdapter("articleImage")
        @JvmStatic
        fun bindArticleImage(view: ImageView, imageUrl: String?) {
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(view.context).load(imageUrl).into(view)
            }
        }
    }
}