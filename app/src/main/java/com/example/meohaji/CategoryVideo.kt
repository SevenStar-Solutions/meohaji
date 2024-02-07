package com.example.meohaji

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryVideo(
    val id: String,
    val publishedAt: String,
    val channelTitle: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val viewCount: Int,
    val likeCount: Int,
    val commentCount: Int,
    val recommendScore: Double
) : Parcelable