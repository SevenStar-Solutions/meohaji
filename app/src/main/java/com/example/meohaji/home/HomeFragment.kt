package com.example.meohaji.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.meohaji.BuildConfig
import com.example.meohaji.NetworkClient.apiService
import com.example.meohaji.databinding.FragmentHomeBinding
import com.example.meohaji.detail.DetailFragment
import com.example.meohaji.detail.DetailTags.DETAIL_CATEGORY
import com.example.meohaji.detail.DetailTags.DETAIL_MOST
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.round
import android.os.Parcelable as Parcelable1

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var backPressedOnce = false

    private val homeAdapter by lazy {
        HomeAdapter(requireContext())
    }

    private val mostPopularVideoList = arrayListOf<MostPopularVideo>()
    private val videoByCategoryList = arrayListOf<CategoryVideo>()
    private val channelByCategoryList = arrayListOf<CategoryChannel>()

    private val list = arrayListOf(
        HomeUiData.Title("지금 가장 인기있는 영상 TOP5"),
        HomeUiData.MostPopularVideos(list = arrayListOf()),
        HomeUiData.Spinner(YoutubeCategory.entries.map { it.str }),
        HomeUiData.Title("해당 카테고리 채널"),
        HomeUiData.CategoryChannels(list = arrayListOf()),
        HomeUiData.TitleWithSpinner("카테고리 인기 영상", SortOrder.entries.map { it.str }),
    )

    private var _finishMostPopularVideoData = MutableLiveData<Boolean>()
    private val finishMostPopularVideoData: LiveData<Boolean> get() = _finishMostPopularVideoData
    private var _finishVideoByCategoryData = MutableLiveData<Boolean>()
    private val finishVideoByCategoryData: LiveData<Boolean> get() = _finishVideoByCategoryData
    private var _finishChannelByCategoryData = MutableLiveData<Boolean>()
    private val finishChannelByCategoryData: LiveData<Boolean> get() = _finishChannelByCategoryData
    val channelIds = StringBuilder()
    private var sortOrderIdx = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        overrideBackAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialCommunicateNetwork()

//        communicateNetwork()

        binding.rvHome.adapter = homeAdapter
        homeAdapter.apply {
            communicateVideoByCategory = object : HomeAdapter.CommunicateVideoByCategory {
                override fun call(id: String, sortOrder: Int) {
                    _finishVideoByCategoryData.value = false
                    _finishChannelByCategoryData.value = false
                    sortOrderIdx = sortOrder
                    communicateVideoByCategory(id)
                }
            }

            detailMostPopularVideo = object : HomeAdapter.DetailMostPopularVideo {
                override fun move(videoData: MostPopularVideo) {
                    setDetailFragment(videoData, DETAIL_MOST)
                }
            }

            detailCategoryVideo = object : HomeAdapter.DetailCategoryVideo {
                override fun move(videoData: CategoryVideo) {
                    setDetailFragment(videoData, DETAIL_CATEGORY)
                }
            }

            sortCategoryVideo = object : HomeAdapter.SortCategoryVideo {
                override fun sort(order: Int) {
                    sortByOrder(order)
                    checkComplete(1)
                }
            }
        }

        finishMostPopularVideoData.observe(viewLifecycleOwner) {
            if (it) checkComplete(0)
        }

        finishChannelByCategoryData.observe(viewLifecycleOwner) {
            if (it) checkComplete(0)
        }

        finishVideoByCategoryData.observe(viewLifecycleOwner) {
            if (it) {
                apiService.channelByCategory(
                    BuildConfig.YOUTUBE_API_KEY,
                    "snippet,statistics",
                    channelIds.toString()
                )?.enqueue(object : Callback<Channel?> {
                    override fun onResponse(
                        call: Call<Channel?>,
                        response: Response<Channel?>
                    ) {
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

                        _finishChannelByCategoryData.value = true
                    }

                    override fun onFailure(call: Call<Channel?>, t: Throwable) {
                        Log.e("#heesoo", "onFailure: ${t.message}")
                    }
                })
            }
        }
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

    private fun initialCommunicateNetwork() {
        _finishMostPopularVideoData.value = false
        _finishVideoByCategoryData.value = false
        _finishChannelByCategoryData.value = false

        communicateMostPopularVideos()
        communicateVideoByCategory("1")
    }

//    private fun communicateNetwork() {
//        CoroutineScope(Dispatchers.Default).launch {
//            async(Dispatchers.IO) {
//                apiService.mostPopularVideos(
//                    BuildConfig.YOUTUBE_API_KEY,
//                    "snippet,statistics",
//                    "mostPopular",
//                    "kr"
//                )?.enqueue(object : Callback<Video?> {
//                    override fun onResponse(call: Call<Video?>, response: Response<Video?>) {
//                        mostPopularVideoList.clear()
//                        response.body()?.items?.forEach { item ->
//                            mostPopularVideoList.add(
//                                MostPopularVideo(
//                                    item.id,
//                                    item.snippet.publishedAt,
//                                    item.snippet.channelTitle,
//                                    item.snippet.title,
//                                    item.snippet.description,
//                                    item.snippet.thumbnails.medium.url,
//                                    item.statistics.viewCount.toInt(),
//                                    item.statistics.likeCount.toInt(),
//                                    item.statistics.commentCount.toInt(),
//                                    calRecommendScore(
//                                        item.snippet.description,
//                                        item.statistics.viewCount.toInt(),
//                                        item.statistics.likeCount.toInt(),
//                                        item.statistics.commentCount.toInt()
//                                    )
//                                )
//                            )
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Video?>, t: Throwable) {
//                        TODO("Not yet implemented")
//                    }
//
//                })
//            }.await()
//
//            async(Dispatchers.IO) {
//                apiService.videoByCategory(
//                    BuildConfig.YOUTUBE_API_KEY,
//                    "snippet,statistics",
//                    "mostPopular",
//                    10,
//                    "kr",
//                    YoutubeCategory.entries[0].id
//                )?.enqueue(object : Callback<Video?> {
//                    override fun onResponse(call: Call<Video?>, response: Response<Video?>) {
//                        videoByCategoryList.clear()
//                        channelIds.clear()
//                        response.body()?.items?.forEach { item ->
//                            videoByCategoryList.add(
//                                CategoryVideo(
//                                    item.id,
//                                    item.snippet.publishedAt,
//                                    item.snippet.channelTitle,
//                                    item.snippet.title,
//                                    item.snippet.description,
//                                    item.snippet.thumbnails.medium.url,
//                                    item.statistics.viewCount.toInt(),
//                                    item.statistics.likeCount.toInt(),
//                                    item.statistics.commentCount.toInt(),
//                                    calRecommendScore(
//                                        item.snippet.description,
//                                        item.statistics.viewCount.toInt(),
//                                        item.statistics.likeCount.toInt(),
//                                        item.statistics.commentCount.toInt()
//                                    )
//                                )
//                            )
//                            channelIds.append(item.snippet.channelID).append(",")
//                        }
//
//                        videoByCategoryList.sortByDescending { it.recommendScore }
//                    }
//
//                    override fun onFailure(call: Call<Video?>, t: Throwable) {
//                        Log.e("#heesoo", "onFailure: ${t.message}")
//                    }
//                })
//            }.await()
//
//            async(Dispatchers.IO) {
//                apiService.channelByCategory(
//                    BuildConfig.YOUTUBE_API_KEY,
//                    "snippet,statistics",
//                    channelIds.toString()
//                )?.enqueue(object : Callback<Channel?> {
//                    override fun onResponse(
//                        call: Call<Channel?>,
//                        response: Response<Channel?>
//                    ) {
//                        channelByCategoryList.clear()
//                        response.body()?.items?.forEach { item ->
//                            channelByCategoryList.add(
//                                CategoryChannel(
//                                    item.id,
//                                    item.snippet.title,
//                                    item.snippet.thumbnails.medium.url
//                                )
//                            )
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Channel?>, t: Throwable) {
//                        Log.e("#heesoo", "onFailure: ${t.message}")
//                    }
//                })
//            }.await()
//
//            withContext(Dispatchers.Main) {
//                checkComplete(0)
//            }
//        }
//    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun communicateMostPopularVideos() {
        apiService.mostPopularVideos(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            "mostPopular",
            "kr"
        )?.enqueue(object : Callback<Video?> {
            override fun onResponse(call: Call<Video?>, response: Response<Video?>) {
                mostPopularVideoList.clear()
                response.body()?.items?.forEach { item ->
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
                _finishMostPopularVideoData.value = true
            }

            override fun onFailure(call: Call<Video?>, t: Throwable) {
                Log.e("#heesoo", "onFailure: ${t.message}")
            }

        })
    }

    private fun communicateVideoByCategory(id: String) {
        apiService.videoByCategory(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            "mostPopular",
            10,
            "kr",
            id
        )?.enqueue(object : Callback<Video?> {
            override fun onResponse(call: Call<Video?>, response: Response<Video?>) {
                videoByCategoryList.clear()
                channelIds.clear()
                response.body()?.items?.forEach { item ->
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

                sortByOrder(sortOrderIdx)
                _finishVideoByCategoryData.value = true
            }

            override fun onFailure(call: Call<Video?>, t: Throwable) {
                Log.e("#heesoo", "onFailure: ${t.message}")
            }
        })
    }

    private fun checkComplete(type: Int) {
        if (finishMostPopularVideoData.value == true && finishVideoByCategoryData.value == true && finishChannelByCategoryData.value == true) {
            when (type) {
                0 -> {
                    homeAdapter.submitList(
                        list.apply {
                            set(1, HomeUiData.MostPopularVideos(mostPopularVideoList))
                            set(4, HomeUiData.CategoryChannels(channelByCategoryList))
                        } + videoByCategoryList.map {
                            HomeUiData.CategoryVideos(it)
                        }
                    )
                }

                1 -> {
                    homeAdapter.submitList(
                        list + videoByCategoryList.map {
                            HomeUiData.CategoryVideos(it)
                        }
                    )
                }
            }
        }
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

    private fun setDetailFragment(item: Parcelable1, key: String) {
        val dialog = DetailFragment.newInstance(item, key)
        dialog.show(requireActivity().supportFragmentManager, "DetailFragment")
    }
}