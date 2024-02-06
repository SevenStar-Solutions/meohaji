package com.example.meohaji.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.meohaji.BuildConfig
import com.example.meohaji.CategoryChannel
import com.example.meohaji.CategoryChannelAdapter
import com.example.meohaji.CategoryVideo
import com.example.meohaji.CategoryVideoAdapter
import com.example.meohaji.MostPopularVideo
import com.example.meohaji.MostPopularVideoAdapter
import com.example.meohaji.NetworkClient.apiService
import com.example.meohaji.SortOrder
import com.example.meohaji.YoutubeCategory
import com.example.meohaji.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.round

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var backPressedOnce = false

    private val mostPopularVideoAdapter by lazy {
        MostPopularVideoAdapter(requireContext())
    }

    private val categoryChannelAdapter by lazy {
        CategoryChannelAdapter(requireContext())
    }

    private val categoryVideoAdapter by lazy {
        CategoryVideoAdapter(requireContext())
    }

    private val categoryList = listOf(
        Pair("영화 & 애니메이션", 1),
        Pair("자동차 & 탈 것", 2),
        Pair("음악", 10),
        Pair("애완동물 & 동물", 15),
        Pair("스포츠", 17),
        Pair("여행 & 행사", 19),
        Pair("게임", 20),
        Pair("일상 & 브이로그", 22),
        Pair("코미디", 23),
        Pair("엔터테인먼트", 24),
        Pair("뉴스 & 정치", 25),
        Pair("정보 & 스타일", 26),
        Pair("교육", 27),
        Pair("과학 & 기술", 28),
    )

    private val sortList = listOf(
        "추천 순",
        "조회수 순",
        "좋아요 순",
        "최신 순"
    )

    private val mostPopularVideoList = arrayListOf<MostPopularVideo>()
    private val _mostPopularVideos = MutableLiveData<List<MostPopularVideo>>()
    private val mostPopularVideos: LiveData<List<MostPopularVideo>> get() = _mostPopularVideos

    private val videoByCategoryList = arrayListOf<CategoryVideo>()
    private val _videoByCategory = MutableLiveData<List<CategoryVideo>>()
    private val videoByCategory: LiveData<List<CategoryVideo>> get() = _videoByCategory

    private val channelByCategoryList = arrayListOf<CategoryChannel>()
    private val _channelByCategory = MutableLiveData<List<CategoryChannel>>()
    private val channelByCategory: LiveData<List<CategoryChannel>> get() = _channelByCategory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        overrideBackAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        communicateMostPopularVideos()

        binding.rvHomeMostPopularVideo.adapter = mostPopularVideoAdapter

        binding.rvHomeCategoryChannel.adapter = categoryChannelAdapter

        binding.rvHomeCategoryVideo.adapter = categoryVideoAdapter

        val adapter1 = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            YoutubeCategory.entries.map { it.str }
        )
        binding.spinnerHomeCategory.adapter = adapter1

        val adapter2 = ArrayAdapter(
            requireContext(),
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            SortOrder.entries.map { it.str }
        )
        binding.spinnerHomeSortVideo.adapter = adapter2

        binding.spinnerHomeCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    communicateVideoByCategory(YoutubeCategory.entries[p2].id)
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

        binding.spinnerHomeSortVideo.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    when (p2) {
                        0 -> {
                            videoByCategoryList.sortByDescending { it.recommendScore }
                        }

                        1 -> {
                            videoByCategoryList.sortByDescending { it.viewCount }
                        }

                        2 -> {
                            videoByCategoryList.sortByDescending { it.likeCount }
                        }

                        else -> {
                            videoByCategoryList.sortBy { it.publishedAt }
                        }
                    }

                    _videoByCategory.value = videoByCategoryList
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

        mostPopularVideos.observe(viewLifecycleOwner) {
            mostPopularVideoAdapter.submitList(it.toList())
        }

        videoByCategory.observe(viewLifecycleOwner) {
            categoryVideoAdapter.submitList(it.toList())
        }

        channelByCategory.observe(viewLifecycleOwner) {
            categoryChannelAdapter.submitList(it.toList())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun communicateMostPopularVideos() {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val videos = getMostPopularVideos()
                mostPopularVideoList.clear()
                videos.items.forEach { item ->
                    mostPopularVideoList.add(
                        MostPopularVideo(
                            item.id,
                            item.snippet.publishedAt,
                            item.snippet.channelTitle,
                            item.snippet.title,
                            item.snippet.description,
                            item.snippet.thumbnails.medium.url,
                            item.statistics.viewCount.toInt(),
                            item.statistics.likeCount.toInt(),
                            item.statistics.commentCount.toInt(),
                            calRecommendScore(
                                item.snippet.description,
                                item.statistics.viewCount.toInt(),
                                item.statistics.likeCount.toInt(),
                                item.statistics.commentCount.toInt()
                            )
                        )
                    )
                }
                _mostPopularVideos.value = mostPopularVideoList
            }
        }
    }

    private fun communicateVideoByCategory(id: String) {
        CoroutineScope(Dispatchers.Main).launch {
            runCatching {
                val videos = getVideoByCategory(id)
                val channelIds = StringBuilder()
                videoByCategoryList.clear()
                channelByCategoryList.clear()
                videos.items.forEach { item ->
                    videoByCategoryList.add(
                        CategoryVideo(
                            item.id,
                            item.snippet.publishedAt,
                            item.snippet.channelTitle,
                            item.snippet.title,
                            item.snippet.description,
                            item.snippet.thumbnails.medium.url,
                            item.statistics.viewCount.toInt(),
                            item.statistics.likeCount.toInt(),
                            item.statistics.commentCount.toInt(),
                            calRecommendScore(
                                item.snippet.description,
                                item.statistics.viewCount.toInt(),
                                item.statistics.likeCount.toInt(),
                                item.statistics.commentCount.toInt()
                            )
                        )
                    )
                    channelIds.append(item.snippet.channelID).append(",")
                }

                val channels = getChannelByCategory(channelIds.toString())
                channels.items.forEach { item ->
                    channelByCategoryList.add(
                        CategoryChannel(
                            item.id,
                            item.snippet.title,
                            item.snippet.thumbnails.medium.url
                        )
                    )
                }

                _videoByCategory.value = videoByCategoryList.sortedByDescending { it.recommendScore }
                _channelByCategory.value = channelByCategoryList
            }
        }
    }

    private suspend fun getMostPopularVideos() = withContext(Dispatchers.IO) {
        apiService.mostPopularVideos(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            "mostPopular",
            "kr"
        )
    }

    private suspend fun getVideoByCategory(id: String) = withContext(Dispatchers.IO) {
        apiService.videoByCategory(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            "mostPopular",
            10,
            "kr",
            id
        )
    }

    private suspend fun getChannelByCategory(id: String) = withContext(Dispatchers.IO) {
        apiService.channelByCategory(BuildConfig.YOUTUBE_API_KEY, "snippet,statistics", id)
    }

    fun calRecommendScore(
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

    private fun overrideBackAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (backPressedOnce) {
                requireActivity().finish() // 애플리케이션 종료
            } else {
                backPressedOnce = true
                Toast.makeText(requireContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

                // 2초 후에 backPressedOnce 변수를 false로 초기화
                Handler(Looper.getMainLooper()).postDelayed({
                    backPressedOnce = false
                }, 2000)
            }
        }
    }
}