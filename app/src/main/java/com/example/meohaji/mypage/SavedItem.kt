package com.example.meohaji.mypage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class SavedItem (
    var channelTitle: String,
    var commentCount: Int,
    var description: String,
    var id: String,
    var likeCount: Int,
    var publishedAt: String,
    var recommendScore: String,
    var thumbnail: String,
    var title: String,
    var viewCount: Int,
): Parcelable {
    var isLike = false
}
