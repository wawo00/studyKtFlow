package com.example.studyktflow.data.model

data class ApiResponse<T>(
    val data: T?,
    val errorCode: Int,
    val errorMsg: String
)
