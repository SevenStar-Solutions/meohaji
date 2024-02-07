package com.example.meohaji.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.meohaji.*
import com.example.meohaji.BuildConfig
import com.example.meohaji.CategoryChannel
import com.example.meohaji.CategoryChannelAdapter
import com.example.meohaji.CategoryVideo
import com.example.meohaji.CategoryVideoAdapter
import com.example.meohaji.Channel
import com.example.meohaji.HomeAdapter
import com.example.meohaji.HomeUiData
import com.example.meohaji.MostPopularVideo
import com.example.meohaji.MostPopularVideoAdapter
import com.example.meohaji.NetworkClient.apiService
import com.example.meohaji.SeachAdapter
import com.example.meohaji.SeachList
import com.example.meohaji.Video
import com.example.meohaji.YoutubeCategory
import com.example.meohaji.databinding.FragmentHomeBinding
import com.example.meohaji.fragment.DetailTags.DETAIL_CATEGORY
import com.example.meohaji.fragment.DetailTags.DETAIL_MOST
import com.google.android.material.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.round
import android.os.Parcelable as Parcelable1

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var backPressedOnce = false

    private val mostPopularVideoAdapter by lazy {
        MostPopularVideoAdapter(requireContext())
    }

    private val categoryVideoAdapter by lazy {
        CategoryVideoAdapter(requireContext())
    }

    private val homeAdapter by lazy {
        HomeAdapter(requireContext())
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

    private val videoByCategoryList = arrayListOf<HomeUiData.CategoryVideos>()

    private val channelByCategoryList = arrayListOf<CategoryChannel>()

    private val list = arrayListOf<HomeUiData>(
        HomeUiData.Title("지금 가장 인기있는 영상 TOP5"),
        HomeUiData.MostPopularVideos(list = arrayListOf()),
        HomeUiData.Spinner(YoutubeCategory.entries.map { it.str }),
        HomeUiData.Title("해당 카테고리 채널"),
        HomeUiData.CategoryChannels(list = arrayListOf()),
        HomeUiData.Title("카테고리 인기 영상"),
    )

    private var clear1 = true
    private var _clear2 = MutableLiveData<Boolean>()
    private val clear2: LiveData<Boolean> get() = _clear2
    private var clear3 = false
    val channelIds = StringBuilder()

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
        communicateVideoByCategory("1")

        binding.rvHome.adapter = homeAdapter
        homeAdapter.communicateVideoByCategory = object : HomeAdapter.CommunicateVideoByCategory {
            override fun call(id: String) {
                _clear2.value = false
                clear3 = false
                communicateVideoByCategory(id)
            }
        }

        homeAdapter.detailMostPopularVideo = object : HomeAdapter.DetailMostPopularVideo {
            override fun move(videoData: MostPopularVideo) {
                setDetailFragment(videoData, DETAIL_MOST)
                Log.i("This is HomeFragment", "MostPopularVideoAdapter Interface : $videoData")
            }

        }

        homeAdapter.detailCategoryVideo = object : HomeAdapter.DetailCategoryVideo {
            override fun move(videoData: CategoryVideo) {
                setDetailFragment(videoData, DETAIL_CATEGORY)
                Log.i("This is HomeFragment", "CategoryVideoAdapter Interface : $videoData")
            }
        }

        clear2.observe(viewLifecycleOwner) {
            if (it) {
                apiService.channelByCategory(
                    BuildConfig.YOUTUBE_API_KEY,
                    "snippet,statistics",
                    channelIds.toString()
                )
                    ?.enqueue(object : Callback<Channel?> {
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

                            clear3 = true
                            checkComplete()
                        }

                        override fun onFailure(call: Call<Channel?>, t: Throwable) {
                            Log.e("#heesoo", "onFailure: ${t.message}")
                        }
                    })
            }
        }

//        // VideoAdapter에서 interface로 받은 데이터를 DetailFragment Dialog로 띄우는 부분
//        mostPopularVideoAdapter.videoClick =
//            object : MostPopularVideoAdapter.MostPopularVideoClick {
//                override fun onClick(videoData: MostPopularVideo) {
//                    setDetailFragment(videoData, DETAIL_MOST)
//                    Log.i("This is HomeFragment", "MostPopularVideoAdapter Interface : $videoData")
//                }
//            }
//        categoryVideoAdapter.videoClick = object : CategoryVideoAdapter.CategoryVideoClick {
//            override fun onClick(videoData: CategoryVideo) {
//                setDetailFragment(videoData, DETAIL_CATEGORY)
//                Log.i("This is HomeFragment", "CategoryVideoAdapter Interface : $videoData")
//            }
//        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun communicateMostPopularVideos() {
        clear1 = false

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
                clear1 = true
                checkComplete()
            }

            override fun onFailure(call: Call<Video?>, t: Throwable) {
                TODO("Not yet implemented")
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
                        HomeUiData.CategoryVideos(
                            video = CategoryVideo(
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
                    )
                    channelIds.append(item.snippet.channelID).append(",")
                }

                _clear2.value = true
                checkComplete()
            }

            override fun onFailure(call: Call<Video?>, t: Throwable) {
                Log.e("#heesoo", "onFailure: ${t.message}")
            }
        })
    }

    private fun checkComplete() {
        if (clear1 && clear2.value == true && clear3) {
            homeAdapter.submitList(
                list.apply {
                    set(1, HomeUiData.MostPopularVideos(mostPopularVideoList))
                    set(4, HomeUiData.CategoryChannels(channelByCategoryList))
                } + videoByCategoryList
            )
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
        val dialog = DetailFragment.newInstance(item,key)
        dialog.show(requireActivity().supportFragmentManager,"DetailFragment")
    }
}