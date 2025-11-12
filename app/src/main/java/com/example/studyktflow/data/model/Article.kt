package com.example.studyktflow.data.model

data class ArticleListResponse(
    val curPage: Int,
    val datas: List<Article>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class Article(
    val id: Int,
    val originId: Int = id,
    val title: String,
    val link: String,
    val author: String,
    val shareUser: String,
    val niceDate: String,
    val desc: String,
    val chapterName: String,
    val superChapterName: String,
    var collect: Boolean,
    val userId: Int = -1
)
