package com.example.meohaji.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meohaji.BuildConfig
import com.example.meohaji.NetworkClient
import kotlinx.coroutines.launch
import kotlin.math.round

class HomeViewModel : ViewModel() {

    private val _homeList = MutableLiveData(
        listOf(
            HomeUiData.Title("지금 가장 인기있는 영상 TOP5"),
            HomeUiData.MostPopularVideos(list = listOf()),
            HomeUiData.Spinner(YoutubeCategory.entries.map { it.str }),
            HomeUiData.Title("해당 카테고리 채널"),
            HomeUiData.CategoryChannels(list = listOf()),
            HomeUiData.TitleWithSpinner("카테고리 인기 영상", SortOrder.entries.map { it.str }),
        )
    )
    val homeList: LiveData<List<HomeUiData>> get() = _homeList

    private val mostPopularVideoList = arrayListOf<VideoForUi>()
    private val videoByCategoryList = arrayListOf<VideoForUi>()
    private val channelByCategoryList = arrayListOf<CategoryChannel>()

    private val channelIds = StringBuilder()
    private var sortOrderIdx = 0

    fun initialCommunicateNetwork() {
        viewModelScope.launch {
            communicateMostPopularVideos()
            communicateVideoByCategory("1")
            checkComplete(ENTIRE_CHANGE)
        }
    }

    fun changeCategory(id: String, sortOrder: Int) {
        viewModelScope.launch {
            sortOrderIdx = sortOrder
            communicateVideoByCategory(id)
            checkComplete(CATEGORY_CHANGE)
        }
    }

    fun sortVideo(sortOrder: Int) {
        sortByOrder(sortOrder)
        checkComplete(SORT_CHANGE)
    }

    private suspend fun communicateMostPopularVideos() {
        val response = NetworkClient.apiService.mostPopularVideos(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            "mostPopular",
            "kr"
        )
        if (response.isSuccessful) {
            mostPopularVideoList.clear()
            response.body()?.items?.forEach { item ->
                mostPopularVideoList.add(
                    VideoForUi(
                        item.id,
                        item.snippet.publishedAt,
                        item.snippet.channelTitle,
                        item.snippet.title,
                        item.snippet.description,
                        item.snippet.thumbnails.medium.url,
                        item.statistics.viewCount.toInt(),
                        item.statistics.likeCount?.toInt() ?: 0,
                        item.statistics.commentCount.toInt(),
                        calRecommendScore(
                            item.snippet.description,
                            item.statistics.viewCount.toInt(),
                            item.statistics.likeCount?.toInt() ?: 0,
                            item.statistics.commentCount.toInt()
                        )
                    )
                )
            }
        } else {
            Log.d("dkj", "is not Successful")
        }

    }

    private suspend fun communicateVideoByCategory(id: String) {
        val response = NetworkClient.apiService.videoByCategory(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            "mostPopular",
            10,
            "kr",
            id
        )
        if (response.isSuccessful) {
            videoByCategoryList.clear()
            channelIds.clear()
            response.body()?.items?.forEach { item ->
                videoByCategoryList.add(
                    VideoForUi(
                        item.id,
                        item.snippet.publishedAt,
                        item.snippet.channelTitle,
                        item.snippet.title,
                        item.snippet.description,
                        item.snippet.thumbnails.medium.url,
                        item.statistics.viewCount.toInt(),
                        item.statistics.likeCount?.toInt() ?: 0,
                        item.statistics.commentCount.toInt(),
                        calRecommendScore(
                            item.snippet.description,
                            item.statistics.viewCount.toInt(),
                            item.statistics.likeCount?.toInt() ?: 0,
                            item.statistics.commentCount.toInt()
                        )
                    )
                )
                channelIds.append(item.snippet.channelID).append(",")
            }
            sortByOrder(sortOrderIdx)

            communicationChannelByCategory()
        }

    }

    private suspend fun communicationChannelByCategory() {
        val response = NetworkClient.apiService.channelByCategory(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            channelIds.toString()
        )
        if (response.isSuccessful) {
            channelByCategoryList.clear()
            response.body()?.items?.forEach { item ->
                channelByCategoryList.add(
                    CategoryChannel(
                        item.id,
                        item.snippet.title,
                        item.snippet.thumbnails.medium.url
                    )
                )
            }
        }

    }

    private fun calRecommendScore(
        description: String,
        viewCount: Int,
        likeCount: Int,
        commentCount: Int
    ): Double {
        val viewScore = viewCount * 0.5 * (1.0 / viewCount.toString().length)
        val likeScore = likeCount * 0.3 * (1.0 / likeCount.toString().length)
        val commentScore = commentCount * 0.2 * (1.0 / commentCount.toString().length)
        val isShorts = description.contains("shorts")

        var totalScore =
            if (isShorts) (viewScore + likeScore + commentScore) / viewScore * 3.3 * 1.5 else (viewScore + likeScore + commentScore) / viewScore * 3.3
        totalScore = round(totalScore * 10) / 10
        return if (totalScore > 5.0) 5.0 else totalScore
    }

    private fun sortByOrder(order: Int) {
        when (order) {
            0 -> {
                videoByCategoryList.sortByDescending { it.recommendScore }
            }

            1 -> {
                videoByCategoryList.sortByDescending { it.viewCount }
            }

            2 -> {
                videoByCategoryList.sortByDescending { it.likeCount }
            }

            3 -> {
                videoByCategoryList.sortByDescending { it.publishedAt }
            }
        }
    }

    private fun checkComplete(type: Int) {
        when (type) {
            0 -> {
                _homeList.value = homeList.value.orEmpty().toMutableList().subList(0, 6).apply {
                    set(1, HomeUiData.MostPopularVideos(mostPopularVideoList))
                    set(4, HomeUiData.CategoryChannels(channelByCategoryList))
                } + videoByCategoryList.map {
                    HomeUiData.CategoryVideos(it)
                }
            }

            1 -> {
                _homeList.value = homeList.value.orEmpty().toMutableList().subList(0, 6).apply {
                    set(4, HomeUiData.CategoryChannels(channelByCategoryList))
                } + videoByCategoryList.map {
                    HomeUiData.CategoryVideos(it)
                }
            }

            2 -> {
                _homeList.value = homeList.value.orEmpty().toMutableList()
                    .subList(0, 6) + videoByCategoryList.map {
                    HomeUiData.CategoryVideos(it)
                }
            }
        }
    }

    companion object {
        const val ENTIRE_CHANGE = 0
        const val CATEGORY_CHANGE = 1
        const val SORT_CHANGE = 2
    }
}