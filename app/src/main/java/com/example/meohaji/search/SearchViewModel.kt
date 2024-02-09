package com.example.meohaji.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meohaji.BuildConfig
import com.example.meohaji.NetworkClient
import com.example.meohaji.home.VideoForUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round

class SearchViewModel: ViewModel() {

    private val _searchVideoList: MutableLiveData<List<SearchList>> = MutableLiveData()
    val searchVideoList: LiveData<List<SearchList>> get() = _searchVideoList

    private val _selectedVideo: MutableLiveData<VideoForUi> = MutableLiveData()
    val selectedVideo: LiveData<VideoForUi> get() = _selectedVideo

    //받아온 데이터를 LiveData에 넣는과정
    fun communicateSearchVideos(search: String) {
        viewModelScope.launch {
            runCatching {
                /**런캐칭이 뭔지 검색해보기*/
                val videos = searchByQueryList(query = search)
                _searchVideoList.value = videos.items.map { item ->
                    SearchList(
                        item.snippet.title,
                        item.snippet.thumbnails.medium.url,
                        item.snippet.channelTitle,
                        item.snippet.publishedAt,
                        item.id.videoId
                    )
                }
            }
        }
    }

    private suspend fun searchByQueryList(query: String) = withContext(Dispatchers.IO) {
        NetworkClient.apiService.searchByQueryList(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet",
            10,
            "date",
            query,
            "kr",
            "video",
        )
    }

    /** 클릭한 아이템 id로 데이터 요청*/
    private suspend fun searchByIdList(id: String) = withContext(Dispatchers.IO) {
        NetworkClient.apiService.searchByIdList(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            id,
        )
    }

    /** List로 넣는 작업*/
    fun communicateIDSearchVideos(videoId: String) {
        viewModelScope.launch {
            runCatching {
                /**런캐칭이 뭔지 검색해보기*/
                val video = searchByIdList(id = videoId)
                _selectedVideo.value = with(video.items[0]) {
                    VideoForUi(
                        id,
                        snippet.publishedAt,
                        snippet.channelTitle,
                        snippet.title,
                        snippet.description,
                        snippet.thumbnails.medium.url,
                        statistics.viewCount.toInt(),
                        statistics.likeCount?.toInt() ?: 0,
                        statistics.commentCount.toInt(),
                        calRecommendScore(
                            snippet.description,
                            statistics.viewCount.toInt(),
                            statistics.likeCount?.toInt() ?: 0,
                            statistics.commentCount.toInt()
                        )
                    )
                }
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
}