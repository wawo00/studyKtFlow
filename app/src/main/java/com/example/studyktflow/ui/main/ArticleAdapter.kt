package com.example.studyktflow.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studyktflow.R
import com.example.studyktflow.data.model.Article

class ArticleAdapter(
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {
    
    private val articles = mutableListOf<Article>()
    
    fun submitList(newArticles: List<Article>) {
        articles.clear()
        articles.addAll(newArticles)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }
    
    override fun getItemCount(): Int = articles.size
    
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvAuthor: TextView = itemView.findViewById(R.id.tvAuthor)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvChapter: TextView = itemView.findViewById(R.id.tvChapter)
        
        fun bind(article: Article) {
            tvTitle.text = article.title
            tvAuthor.text = article.author.ifEmpty { article.shareUser }
            tvTime.text = article.niceDate
            tvChapter.text = "${article.superChapterName} / ${article.chapterName}"
            
            itemView.setOnClickListener {
                onItemClick(article)
            }
        }
    }
}
