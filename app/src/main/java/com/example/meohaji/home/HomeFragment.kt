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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meohaji.databinding.FragmentHomeBinding
import com.example.meohaji.detail.BtnClick
import com.example.meohaji.detail.DetailFragment

interface BtnClick2 {
    fun clickFromHome()
}

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var backPressedOnce = false

    var btnClick2: BtnClick2? = null

    private val homeAdapter by lazy {
        HomeAdapter(requireContext())
    }

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(requireContext())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BtnClick2) {
            btnClick2 = context
        }
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
        binding.rvHome.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val totalItemCount = recyclerView.adapter?.itemCount?.minus(1)
                if (lastItemPosition + 2 == totalItemCount && !homeViewModel.isLoading) {
                    homeViewModel.additionalCommunicateNetwork()
                }
            }
        })

        homeAdapter.apply {
            communicateVideoByCategory = object : HomeAdapter.CommunicateVideoByCategory {
                override fun call(id: String, sortOrder: Int) {
                    homeViewModel.changeCategory(id, sortOrder)
                }
            }

            detailMostPopularVideo = object : HomeAdapter.DetailMostPopularVideo {
                override fun move(videoData: VideoForUi) {
                    setDetailFragment(videoData)
                }
            }

            detailCategoryVideo = object : HomeAdapter.DetailCategoryVideo {
                override fun move(videoData: VideoForUi) {
                    setDetailFragment(videoData)
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
            isLoading = false
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

    private fun setDetailFragment(item: VideoForUi) {
        val dialog = DetailFragment.newInstance(item)
        dialog.btnClick = object : BtnClick {
            override fun click() {
                btnClick2?.clickFromHome()
            }
        }
        dialog.show(requireActivity().supportFragmentManager, "DetailFragment")
    }
}