package com.example.meohaji.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchList(
    val title: String,
    var thumbnail: String,
    val channelTitle: String,
    val publishedAt: String,
    val videoId: String,
//    val score: String,
//    val favorit: Boolean
) : Parcelable
