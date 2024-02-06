package com.example.meohaji

data class CategoryVideo(
    val id: String,
    val publishedAt: String,
    val channelTitle: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val viewCount: Int,
    val likeCount: Int,
    val commentCount: Int
)
