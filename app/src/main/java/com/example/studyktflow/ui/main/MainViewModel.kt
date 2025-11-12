package com.example.studyktflow.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyktflow.data.model.Article
import com.example.studyktflow.data.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ArticleListState {
    object Idle : ArticleListState()
    object Loading : ArticleListState()
    data class Success(val articles: List<Article>) : ArticleListState()
    data class Error(val message: String) : ArticleListState()
}

class MainViewModel : ViewModel() {
    
    private val repository = ArticleRepository()
    
    private val _articleListState = MutableStateFlow<ArticleListState>(ArticleListState.Idle)
    val articleListState: StateFlow<ArticleListState> = _articleListState.asStateFlow()
    
    private var currentPage = 0
    
    fun loadArticles(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 0
        }
        
        viewModelScope.launch {
            _articleListState.value = ArticleListState.Loading
            repository.getArticleList(currentPage).collect { result ->
                result.onSuccess { articles ->
                    _articleListState.value = ArticleListState.Success(articles)
                    currentPage++
                }.onFailure { exception ->
                    _articleListState.value = ArticleListState.Error(exception.message ?: "加载失败")
                }
            }
        }
    }
}
