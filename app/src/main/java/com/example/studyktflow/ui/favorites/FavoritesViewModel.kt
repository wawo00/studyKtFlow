package com.example.studyktflow.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyktflow.data.model.Article
import com.example.studyktflow.data.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FavoritesState {
    object Idle : FavoritesState()
    object Loading : FavoritesState()
    data class Success(val articles: List<Article>) : FavoritesState()
    data class Error(val message: String) : FavoritesState()
}

class FavoritesViewModel : ViewModel() {
    
    private val repository = ArticleRepository()
    
    private val _favoritesState = MutableStateFlow<FavoritesState>(FavoritesState.Idle)
    val favoritesState: StateFlow<FavoritesState> = _favoritesState.asStateFlow()
    
    private var currentPage = 0
    
    fun loadFavorites(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 0
        }
        
        viewModelScope.launch {
            _favoritesState.value = FavoritesState.Loading
            repository.getCollectList(currentPage).collect { result ->
                result.onSuccess { articles ->
                    _favoritesState.value = FavoritesState.Success(articles)
                    currentPage++
                }.onFailure { exception ->
                    _favoritesState.value = FavoritesState.Error(exception.message ?: "加载失败")
                }
            }
        }
    }
}
