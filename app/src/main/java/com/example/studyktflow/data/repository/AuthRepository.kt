package com.example.studyktflow.data.repository

import com.example.studyktflow.data.model.ApiResponse
import com.example.studyktflow.data.model.User
import com.example.studyktflow.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepository {
    
    private val apiService = RetrofitClient.apiService
    
    fun login(username: String, password: String): Flow<Result<User>> = flow {
        try {
            val response = apiService.login(username, password)
            if (response.errorCode == 0 && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    fun register(username: String, password: String, repassword: String): Flow<Result<User>> = flow {
        try {
            val response = apiService.register(username, password, repassword)
            if (response.errorCode == 0 && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.errorMsg)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
}
