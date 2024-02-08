package com.example.meohaji.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.meohaji.databinding.FragmentHomeBinding
import com.example.meohaji.detail.BtnClick
import com.example.meohaji.detail.DetailFragment
import com.example.meohaji.detail.DetailTags.DETAIL_CATEGORY
import com.example.meohaji.detail.DetailTags.DETAIL_MOST
import android.os.Parcelable as Parcelable1

interface BtnClick2 {
    fun click()
}

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var backPressedOnce = false

    var btnClick2: BtnClick2? = null

    private val homeAdapter by lazy {
        HomeAdapter(requireContext())
    }

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        btnClick2 = context as BtnClick2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        overrideBackAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.initialCommunicateNetwork()

        initViewModel()
        initView()
    }

    private fun initView() {
        binding.rvHome.adapter = homeAdapter
        homeAdapter.apply {
            communicateVideoByCategory = object : HomeAdapter.CommunicateVideoByCategory {
                override fun call(id: String, sortOrder: Int) {
                    homeViewModel.changeCategory(id, sortOrder)
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
                    homeViewModel.sortVideo(order)
                }
            }
        }
    }

    private fun initViewModel() = with(homeViewModel) {
        homeList.observe(viewLifecycleOwner) {
            homeAdapter.submitList(it.toList())
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

    private fun setDetailFragment(item: Parcelable1, key: String) {
        val dialog = DetailFragment.newInstance(item, key)
        dialog.btnClick = object : BtnClick {
            override fun click() {
                btnClick2?.click()
            }
        }
        dialog.show(requireActivity().supportFragmentManager, "DetailFragment")
    }
}