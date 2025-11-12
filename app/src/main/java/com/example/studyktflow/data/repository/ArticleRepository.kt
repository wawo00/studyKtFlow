package com.example.studyktflow.data.repository

import com.example.studyktflow.data.model.Article
import com.example.studyktflow.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ArticleRepository {
    
    private val apiService = RetrofitClient.apiService
    
    fun getArticleList(page: Int): Flow<Result<List<Article>>> = flow {
        try {
            val response = apiService.getArticleList(page)
            if (response.errorCode == 0 && response.data != null) {
                emit(Result.success(response.data.datas))
            } else {
                emit(Result.failure(Exception(response.errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    fun collectArticle(id: Int): Flow<Result<Unit>> = flow {
        try {
            val response = apiService.collectArticle(id)
            if (response.errorCode == 0) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception(response.errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    fun uncollectArticle(id: Int): Flow<Result<Unit>> = flow {
        try {
            val response = apiService.uncollectArticle(id)
            if (response.errorCode == 0) {
                emit(Result.success(Unit))
            } else {
                emit(Result.failure(Exception(response.errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    fun getCollectList(page: Int): Flow<Result<List<Article>>> = f  low {
        try {
            val response = apiService.getCollectList(page)
            if (response.errorCode == 0 && response.data != null) {
                emit(Result.success(response.data.datas))
            } else {
                emit(Result.failure(Exception(response.errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
