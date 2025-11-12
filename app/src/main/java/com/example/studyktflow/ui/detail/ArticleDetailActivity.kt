package com.example.studyktflow.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyktflow.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ArticleDetailActivity : AppCompatActivity() {
    
    private val viewModel: ArticleDetailViewModel by viewModels()
    
    private lateinit var tvTitle: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvChapter: TextView
    private lateinit var tvDesc: TextView
    private lateinit var fabCollect: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    
    private var articleId = 0
    private var articleOriginId = 0
    private var isCollected = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)
        
        title = getString(R.string.article_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        loadArticleData()
        observeViewModel()
    }
    
    private fun initViews() {
        tvTitle = findViewById(R.id.tvTitle)
        tvAuthor = findViewById(R.id.tvAuthor)
        tvTime = findViewById(R.id.tvTime)
        tvChapter = findViewById(R.id.tvChapter)
        tvDesc = findViewById(R.id.tvDesc)
        fabCollect = findViewById(R.id.fabCollect)
        progressBar = findViewById(R.id.progressBar)
        
        fabCollect.setOnClickListener {
            viewModel.toggleCollect(articleOriginId, isCollected)
        }
    }
    
    private fun loadArticleData() {
        articleId = intent.getIntExtra("article_id", 0)
        articleOriginId = intent.getIntExtra("article_origin_id", articleId)
        isCollected = intent.getBooleanExtra("article_collect", false)
        
        tvTitle.text = intent.getStringExtra("article_title")
        tvAuthor.text = getString(R.string.author, intent.getStringExtra("article_author"))
        tvTime.text = getString(R.string.time, intent.getStringExtra("article_date"))
        tvChapter.text = getString(R.string.chapter, intent.getStringExtra("article_chapter"))
        tvDesc.text = intent.getStringExtra("article_desc")
        
        updateCollectButton()
    }
    
    private fun updateCollectButton() {
        fabCollect.isSelected = isCollected
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.collectState.collect { state ->
                when (state) {
                    is CollectState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                    is CollectState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is CollectState.Success -> {
                        progressBar.visibility = View.GONE
                        isCollected = state.collected
                        updateCollectButton()
                        val message = if (isCollected) {
                            R.string.collect_success
                        } else {
                            R.string.uncollect_success
                        }
                        Toast.makeText(this@ArticleDetailActivity, message, Toast.LENGTH_SHORT).show()
                    }
                    is CollectState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@ArticleDetailActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
