package com.example.studyktflow.data.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    private const val BASE_URL = "https://www.wanandroid.com/"
    
    private var cookieJar: PersistentCookieJar? = null
    
    fun initialize(context: Context) {
        cookieJar = PersistentCookieJar(context)
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar ?: throw IllegalStateException("RetrofitClient not initialized"))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    
    fun clearCookies() {
        cookieJar?.clearCookies()
    }
}
