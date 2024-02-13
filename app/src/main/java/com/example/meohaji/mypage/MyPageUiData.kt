package com.example.meohaji.mypage

import com.example.meohaji.home.VideoForUi

sealed class MyPageUiData {
    data class Profile(
        val name: String?,
        val image: String?
    ): MyPageUiData()

    data object Title : MyPageUiData()

    data class Video(
        val video: VideoForUi
    ): MyPageUiData()
}