package com.example.meohaji.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import com.example.meohaji.*
import com.example.meohaji.databinding.FragmentHomeBinding

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
        "Film & Animation",
        "Autos & Vehicles",
        "Music",
        "Pets & Animals",
        "Sports",
        "Short Movies",
        "Travel & Events",
        "Gaming",
        "Videoblogging",
        "People & Blogs",
        "Comedy",
        "Entertainment",
        "News & Politics",
        "Howto & Style",
        "Education",
        "Science & Technology",
        "Nonprofits & Activism",
        "Movies",
        "Anime/Animation",
        "Action/Adventure",
        "Classics",
        "Comedy",
        "Documentary",
        "Drama",
        "Family",
        "Foreign",
        "Horror",
        "Sci-Fi/Fantasy",
        "Thriller",
        "Shorts",
        "Shows",
        "Trailers"
    )

    private val sortList = listOf(
        "추천 순",
        "조회수 순",
        "좋아요 순",
        "최신 순"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        overrideBackAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvHomeMostPopularVideo.adapter = mostPopularVideoAdapter
        mostPopularVideoAdapter.submitList(dummy)

        binding.rvHomeCategoryChannel.adapter = categoryChannelAdapter
        categoryChannelAdapter.submitList(dummy2)

        binding.rvHomeCategoryVideo.adapter = categoryVideoAdapter
        categoryVideoAdapter.submitList(dummy3)

        val adapter1 = ArrayAdapter(
            requireContext(),
            com.bumptech.glide.R.layout.support_simple_spinner_dropdown_item,
            categoryList
        )
        binding.spinnerHomeCategory.adapter = adapter1

        val adapter2 = ArrayAdapter(
            requireContext(),
            com.bumptech.glide.R.layout.support_simple_spinner_dropdown_item,
            sortList
        )
        binding.spinnerHomeSortVideo.adapter = adapter2

        // MostPopularVideoAdapter에서 interface로 받은 데이터를 DetailFragment로 넘기는 뷰뷴
        mostPopularVideoAdapter.goDetail = object : GoDetail {
            override fun sendData(v: View, dummy: DummyDetail) {
                val detailFragment = DetailFragment.newInstance(dummy)
                    requireActivity().supportFragmentManager.commit {
                        replace(R.id.frame_main,detailFragment)
                        setReorderingAllowed(true)
                        addToBackStack(null)
                    }
                Log.i("This is HomeFragment","Adapter Interface : $dummy")
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
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

    private val dummy = listOf<MostPopularVideo>(
        MostPopularVideo(
            id = "kW_z-NMuZIU",
            publishedAt = "2024-02-02T21:30:04Z",
            channelTitle = "쿠팡플레이 스포츠",
            title = "[2023 AFC 카타르 아시안컵] 2023 AFC 카타르 아시안컵 호주 VS 대한민국 풀 하이라이트",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg"
        ),
        MostPopularVideo(
            id = "kW_z-NMuZIU",
            publishedAt = "2024-02-02T21:30:04Z",
            channelTitle = "쿠팡플레이 스포츠",
            title = "[2023 AFC 카타르 아시안컵] 2023 AFC 카타르 아시안컵 호주 VS 대한민국 풀 하이라이트",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg"
        )
    )

    private val dummy2 = listOf<CategoryChannel>(
        CategoryChannel(
            id = "kW_z-NMuZIU",
            title = "쿠팡플레이 스포츠",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg"
        ),
        CategoryChannel(
            id = "kW_z-NMuZIU",
            title = "쿠팡플레이 스포츠",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg"
        ),
        CategoryChannel(
            id = "kW_z-NMuZIU",
            title = "쿠팡플레이 스포츠",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg"
        ),
        CategoryChannel(
            id = "kW_z-NMuZIU",
            title = "쿠팡플레이 스포츠",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg"
        ),
        CategoryChannel(
            id = "kW_z-NMuZIU",
            title = "쿠팡플레이 스포츠",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg"
        ),
        CategoryChannel(
            id = "kW_z-NMuZIU",
            title = "쿠팡플레이 스포츠",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg"
        )
    )

    private val dummy3 = listOf<CategoryVideo>(
        CategoryVideo(
            id = "kW_z-NMuZIU",
            publishedAt = "2024-02-02T21:30:04Z",
            channelTitle = "쿠팡플레이 스포츠",
            title = "[2023 AFC 카타르 아시안컵] 2023 AFC 카타르 아시안컵 호주 VS 대한민국 풀 하이라이트",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/2SHg5ASsTN0/mqdefault.jpg"
        ),
        CategoryVideo(
            id = "kW_z-NMuZIU",
            publishedAt = "2024-02-02T21:30:04Z",
            channelTitle = "쿠팡플레이 스포츠",
            title = "[2023 AFC 카타르 아시안컵] 2023 AFC 카타르 아시안컵 호주 VS 대한민국 풀 하이라이트",
            description = "쿠팡플레이에서 2023 AFC 카타르 아시안컵 생중계와 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/2SHg5ASsTN0/mqdefault.jpg"
        )
    )
}