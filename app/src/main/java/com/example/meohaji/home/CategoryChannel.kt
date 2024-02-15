package com.example.meohaji.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryChannel(
    val id: String,
    val title: String,
    val thumbnail: String,
    val description: String,
    val viewCount: Long,
    val subscriberCount: Long,
    val videoCount: Long,
    val customUrl: String
): Parcelable