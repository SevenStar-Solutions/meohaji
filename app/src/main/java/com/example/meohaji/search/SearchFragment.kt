package com.example.meohaji.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meohaji.databinding.FragmentSearchBinding
import com.example.meohaji.detail.DetailFragment

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var backPressedOnce = false
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter(requireContext())
    }

    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        overrideBackAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
    }

    private fun initViewModel() = with(searchViewModel) {
        searchVideoList.observe(viewLifecycleOwner) {
            searchAdapter.submitList(it.toList())
        }

        selectedVideo.observe(viewLifecycleOwner) {
            val dialog = DetailFragment.newInstance(it)
            dialog.show(requireActivity().supportFragmentManager, "DetailFragment")
        }
    }

    private fun initView() {
        binding.rvSearch.adapter = searchAdapter

        /**무한스크롤, 추가필요*/
        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val totalItemCount = recyclerView.adapter?.itemCount?.minus(1)
                if (lastItemPosition + 2 == totalItemCount) {
                    searchViewModel.communicateSearchVideos(binding.etSearchFragmentSearch.text.toString())
                }
            }
        })

        //키보드에서 엔터로 검색시작
        binding.etSearchFragmentSearch.setOnEditorActionListener { textView, action, event ->
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                searchViewModel.communicateSearchVideos(binding.etSearchFragmentSearch.text.toString())

                //키보드 숨기기
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etSearchFragmentSearch.windowToken, 0)
            }
            false
        }

        /**클릭이벤트 VideoID로 API를 받아오고 DetailFragment로 데이터 보내고 실행*/
        searchAdapter.videoClick = object : SearchAdapter.SearchVideoClick {
            override fun onClick(videoData: SearchList) {
                searchViewModel.communicateIDSearchVideos(videoData.videoId)
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
}



