package com.example.meohaji

import retrofit2.http.Query

data class SeachList(
    val title: String,
    var thumbnail: String,
    val channelTitle: String,
    val publishedAt: String,
//    val score: String,
//    val favorit: Boolean
)
