package com.example.meohaji.search

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.http.Query

@Parcelize
data class HistoryList(
    val query: String
):  Parcelable
