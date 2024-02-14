package com.example.meohaji.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryChannel(
    val id: String,
    val title: String,
    val thumbnail: String,
    val description: String,
    val viewCount: Int,
    val subscriberCount: Int,
    val videoCount: Int,
    val customUrl: String
): Parcelable