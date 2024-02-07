package com.example.meohaji

sealed class HomeUiData {
    data class Title(
        val title: String
    ): HomeUiData()

    data class MostPopularVideos(
        val list: List<MostPopularVideo>
    ): HomeUiData()

    data class CategoryChannels(
        val list: List<CategoryChannel>
    ): HomeUiData()

    data class CategoryVideos(
        val video: CategoryVideo
    ): HomeUiData()

    data class Spinner(
        val categories: List<String>
    ): HomeUiData()

    data class TitleWithSpinner(
        val title: String,
        val categories: List<String>
    ): HomeUiData()
}