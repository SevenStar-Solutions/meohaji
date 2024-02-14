package com.example.meohaji.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meohaji.BuildConfig
import com.example.meohaji.NetworkClient
import com.example.meohaji.Utils
import kotlinx.coroutines.launch

class HomeViewModel(private val context: Context) : ViewModel() {

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
    private val additionalVideoList = arrayListOf<VideoForUi>()

    private val channelIds = StringBuilder()
    private var pageToken: String? = null
    private var sortOrderIdx = 0
    private var categoryId = "1"
    var isLoading = false

    // 가장 처음에 사용되는 함수
    fun initialCommunicateNetwork() {
        viewModelScope.launch {
            communicateMostPopularVideos()
            communicateVideoByCategory(categoryId)
            checkComplete(ENTIRE_CHANGE)
            popularVideoAverage()
        }
    }

    // 카테고리 변경 시 사용되는 함수
    fun changeCategory(id: String, sortOrder: Int) {
        viewModelScope.launch {
            pageToken = null
            sortOrderIdx = sortOrder
            categoryId = id
            communicateVideoByCategory(categoryId)
            checkComplete(CATEGORY_CHANGE)
        }
    }

    // 무한 스크롤 시 사용되는 함수
    fun additionalCommunicateNetwork() {
        viewModelScope.launch {
            isLoading = true
            scrollCommunicateVideoByCategory()
        }
    }

    // 영상 정렬시 사용되는 함수
    fun sortVideo(sortOrder: Int) {
        sortByOrder(sortOrder)
        checkComplete(SORT_CHANGE)
    }

    // 가장 인기있는 영상 가져오는 함수
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
                        Utils.calRecommendScore(
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

    // 카테고리 영상을 받아오는 함수
    private suspend fun communicateVideoByCategory(id: String) {
        val response = NetworkClient.apiService.videoByCategory(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            "mostPopular",
            10,
            "kr",
            pageToken,
            id
        )

        if (response.isSuccessful) {
            videoByCategoryList.clear()
            additionalVideoList.clear()
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
                        Utils.calRecommendScore(
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
            pageToken = response.body()?.nextPageToken.toString()
            communicationChannelByCategory()
        }
    }

    // 스크롤 시 카테고리 영상을 받아오는 함수
    private suspend fun scrollCommunicateVideoByCategory() {
        val response = NetworkClient.apiService.videoByCategory(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            "mostPopular",
            10,
            "kr",
            pageToken,
            categoryId
        )

        if (response.isSuccessful) {
            response.body()?.items?.forEach { item ->
                additionalVideoList.add(
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
                        Utils.calRecommendScore(
                            item.snippet.description,
                            item.statistics.viewCount.toInt(),
                            item.statistics.likeCount?.toInt() ?: 0,
                            item.statistics.commentCount.toInt()
                        )
                    )
                )
            }
            checkComplete(SCROLL_DOWN)
        }
    }

    // 카테고리 채널을 받아오는 함수
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
                        item.snippet.thumbnails.high.url,
                        item.snippet.description,
                        item.statistics.viewCount.toInt(),
                        item.statistics.subscriberCount.toInt(),
                        item.statistics.videoCount.toInt(),
                        item.snippet.customURL
                    )
                )
            }
        }
    }

    // order에 맞춰 영상을 정렬하는 함수
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

    // 타입에 맞춰 UI 데이터를 갱신하는 함수
    private fun checkComplete(type: Int) {
        when (type) {
            ENTIRE_CHANGE -> {
                _homeList.value = homeList.value.orEmpty().toMutableList().subList(0, 6).apply {
                    set(1, HomeUiData.MostPopularVideos(mostPopularVideoList))
                    set(4, HomeUiData.CategoryChannels(channelByCategoryList))
                } + videoByCategoryList.map {
                    HomeUiData.CategoryVideos(it)
                }
            }

            CATEGORY_CHANGE -> {
                _homeList.value = homeList.value.orEmpty().toMutableList().subList(0, 6).apply {
                    set(4, HomeUiData.CategoryChannels(channelByCategoryList))
                } + videoByCategoryList.map {
                    HomeUiData.CategoryVideos(it)
                }
            }

            SORT_CHANGE -> {
                _homeList.value = homeList.value.orEmpty().toMutableList()
                    .subList(0, 6) + (videoByCategoryList + additionalVideoList).map {
                    HomeUiData.CategoryVideos(it)
                }
            }

            SCROLL_DOWN -> {
                _homeList.value = homeList.value.orEmpty().toMutableList().apply {
                    addAll(additionalVideoList.map {
                        HomeUiData.CategoryVideos(it)
                    })
                }
            }
        }
    }

    // 인기 영상 Top5 평균 저장하는 함수
    private fun popularVideoAverage() {
        Utils.saveCounts(
            context,
            mostPopularVideoList.map { it.viewCount }.average().toInt(),
            mostPopularVideoList.map { it.likeCount }.average().toInt(),
            mostPopularVideoList.map { it.commentCount }.average().toInt()
        )
    }

    companion object {
        const val ENTIRE_CHANGE = 0
        const val CATEGORY_CHANGE = 1
        const val SORT_CHANGE = 2
        const val SCROLL_DOWN = 3
    }
}