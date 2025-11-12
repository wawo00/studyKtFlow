package com.example.studyktflow.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.studyktflow.R
import com.example.studyktflow.ui.detail.ArticleDetailActivity
import com.example.studyktflow.ui.favorites.FavoritesActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var fabFavorites: FloatingActionButton
    
    private lateinit var adapter: ArticleAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        title = getString(R.string.home)
        
        initViews()
        setupRecyclerView()
        observeViewModel()
        
        viewModel.loadArticles()
    }
    
    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)
        fabFavorites = findViewById(R.id.fabFavorites)
        
        swipeRefresh.setOnRefreshListener {
            viewModel.loadArticles(refresh = true)
        }
        
        fabFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }
    }
    
    private fun setupRecyclerView() {
        adapter = ArticleAdapter { article ->
            val intent = Intent(this, ArticleDetailActivity::class.java).apply {
                putExtra("article_id", article.id)
                putExtra("article_origin_id", article.originId)
                putExtra("article_title", article.title)
                putExtra("article_link", article.link)
                putExtra("article_author", article.author.ifEmpty { article.shareUser })
                putExtra("article_date", article.niceDate)
                putExtra("article_chapter", "${article.superChapterName} / ${article.chapterName}")
                putExtra("article_desc", article.desc)
                putExtra("article_collect", article.collect)
            }
            startActivity(intent)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.articleListState.collect { state ->
                when (state) {
                    is ArticleListState.Idle -> {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false
                    }
                    is ArticleListState.Loading -> {
                        if (!swipeRefresh.isRefreshing) {
                            progressBar.visibility = View.VISIBLE
                        }
                    }
                    is ArticleListState.Success -> {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false
                        adapter.submitList(state.articles)
                    }
                    is ArticleListState.Error -> {
                        progressBar.visibility = View.GONE
                        swipeRefresh.isRefreshing = false
                        Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh the list when returning from detail page to update collect status
        viewModel.loadArticles(refresh = true)
    }
}
