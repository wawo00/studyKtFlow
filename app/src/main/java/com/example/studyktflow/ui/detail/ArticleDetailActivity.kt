package com.example.studyktflow.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyktflow.R
import com.example.studyktflow.databinding.ActivityArticleDetailBinding
import kotlinx.coroutines.launch

class ArticleDetailActivity : AppCompatActivity() {
    
    private val viewModel: ArticleDetailViewModel by viewModels()
    private lateinit var binding: ActivityArticleDetailBinding

    private var articleId = 0
    private var articleOriginId = 0
    private var isCollected = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.article_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        initViews()
        loadArticleData()
        observeViewModel()
    }
    
    private fun initViews() {
        binding.fabCollect.setOnClickListener {
            // Pass both article id and originId so ViewModel can call the correct API
            viewModel.toggleCollect(articleId, articleOriginId, isCollected)
        }
    }
    
    private fun loadArticleData() {
        articleId = intent.getIntExtra("article_id", 0)
        articleOriginId = intent.getIntExtra("article_origin_id", articleId)
        isCollected = intent.getBooleanExtra("article_collect", false)
        
        binding.tvTitle.text = intent.getStringExtra("article_title")
        binding.tvAuthor.text = getString(R.string.author, intent.getStringExtra("article_author"))
        binding.tvTime.text = getString(R.string.time, intent.getStringExtra("article_date"))
        binding.tvChapter.text = getString(R.string.chapter, intent.getStringExtra("article_chapter"))
        binding.tvDesc.text = intent.getStringExtra("article_desc")

        updateCollectButton()
    }
    
    private fun updateCollectButton() {
        binding.fabCollect.isSelected = isCollected
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.collectState.collect { state ->
                when (state) {
                    is CollectState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is CollectState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is CollectState.Success -> {
                        binding.progressBar.visibility = View.GONE
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
                        binding.progressBar.visibility = View.GONE
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
