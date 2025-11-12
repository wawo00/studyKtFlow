package com.example.studyktflow.data.network

import com.example.studyktflow.data.model.ApiResponse
import com.example.studyktflow.data.model.Article
import com.example.studyktflow.data.model.ArticleListResponse
import com.example.studyktflow.data.model.User
import retrofit2.http.*

interface ApiService {
    
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<User>
    
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): ApiResponse<User>
    
    @GET("article/list/{page}/json")
    suspend fun getArticleList(
        @Path("page") page: Int
    ): ApiResponse<ArticleListResponse>
    
    @POST("lg/collect/{id}/json")
    suspend fun collectArticle(
        @Path("id") id: Int
    ): ApiResponse<Any>
    
    @POST("lg/uncollect_originId/{id}/json")
    suspend fun uncollectArticle(
        @Path("id") id: Int
    ): ApiResponse<Any>
    
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectList(
        @Path("page") page: Int
    ): ApiResponse<ArticleListResponse>
}
