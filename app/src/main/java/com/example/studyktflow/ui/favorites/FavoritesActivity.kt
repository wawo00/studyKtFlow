package com.example.studyktflow.ui.favorites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studyktflow.R
import com.example.studyktflow.databinding.ActivityFavoritesBinding
import com.example.studyktflow.ui.detail.ArticleDetailActivity
import com.example.studyktflow.ui.main.ArticleAdapter
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {
    
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var binding: ActivityFavoritesBinding
    private lateinit var adapter: ArticleAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.my_favorites)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        setupRecyclerView()
        observeViewModel()
        
        viewModel.loadFavorites()
    }
    
    private fun initViews() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadFavorites(refresh = true)
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
                putExtra("article_collect", true) // favorites are always collected
            }
            startActivity(intent)
        }
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.favoritesState.collect { state ->
                when (state) {
                    is FavoritesState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is FavoritesState.Loading -> {
                        if (!binding.swipeRefresh.isRefreshing) {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                    is FavoritesState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        adapter.submitList(state.articles)
                    }
                    is FavoritesState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        Toast.makeText(this@FavoritesActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh the list when returning from detail page
        viewModel.loadFavorites(refresh = true)
    }
}
