package com.example.studyktflow.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studyktflow.data.model.Article
import com.example.studyktflow.databinding.ItemArticleBinding

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
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }
    
    override fun getItemCount(): Int = articles.size
    
    inner class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.tvTitle.text = article.title
            binding.tvAuthor.text = article.author.ifEmpty { article.shareUser }
            binding.tvTime.text = article.niceDate
            binding.tvChapter.text = "${article.superChapterName} / ${article.chapterName}"

            binding.root.setOnClickListener {
                onItemClick(article)
            }
        }
    }
}
