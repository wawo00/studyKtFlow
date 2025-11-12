package com.example.studyktflow.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyktflow.data.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CollectState {
    object Idle : CollectState()
    object Loading : CollectState()
    data class Success(val collected: Boolean) : CollectState()
    data class Error(val message: String) : CollectState()
}

class ArticleDetailViewModel : ViewModel() {
    
    private val repository = ArticleRepository()
    
    private val _collectState = MutableStateFlow<CollectState>(CollectState.Idle)
    val collectState: StateFlow<CollectState> = _collectState.asStateFlow()
    
    // Accept both the article id and the originId because the backend uses different ids
    // for collect and uncollect endpoints: collect uses the article `id`, while
    // uncollect (from collect list) uses the `originId`.
    fun toggleCollect(articleId: Int, originId: Int, currentCollectState: Boolean) {
        viewModelScope.launch {
            _collectState.value = CollectState.Loading
            val flow = if (currentCollectState) {
                // When currently collected, call uncollect with originId
                repository.uncollectArticle(originId)
            } else {
                // When not collected, call collect with the article id
                repository.collectArticle(articleId)
            }
            
            flow.collect { result ->
                result.onSuccess {
                    _collectState.value = CollectState.Success(!currentCollectState)
                }.onFailure { exception ->
                    _collectState.value = CollectState.Error(exception.message ?: "操作失败")
                }
            }
        }
    }
}
