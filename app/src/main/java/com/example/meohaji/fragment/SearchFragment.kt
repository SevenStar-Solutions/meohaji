package com.example.meohaji.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import com.example.meohaji.SeachAdapter
import com.example.meohaji.SeachList
import com.example.meohaji.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var backPressedOnce = false
    private val adapter: SeachAdapter by lazy {
        SeachAdapter(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_search, container, false)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        binding.etSeachfragmentSeach.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                /** 여기다 검색 기능 추가하시면 됩니다 */
                hideSoftKeyBoard()
                return@setOnEditorActionListener true
            }
            false
        }

        overrideBackAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSeachRecyclerview.adapter= adapter
        adapter.submitList(dummy)


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

    private val dummy = listOf<SeachList>(
        SeachList(
            title = "[2023 AFC 카타르 아시안컵] 2023 AFC 카타르 아시안컵 호주 VS 대한민국 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg",
            channel = "쿠팡플레이 스포츠",
            time = "2024-02-02T21:30:04Z",
            score = "1",
            favorit = false
        ),
        SeachList(
            title = "[2023 AFC 카타르 아시안컵] 2023 AFC 카타르 아시안컵 호주 VS 대한민국 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg",
            channel = "쿠팡플레이 스포츠",
            time = "2024-02-02T21:30:04Z",
            score = "1",
            favorit = false
        ),
        SeachList(
            title = "[2023 AFC 카타르 아시안컵] 2023 AFC 카타르 아시안컵 호주 VS 대한민국 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg",
            channel = "쿠팡플레이 스포츠",
            time = "2024-02-02T21:30:04Z",
            score = "1",
            favorit = false
        ),
        SeachList(
            title = "[2023 AFC 카타르 아시안컵] 2023 AFC 카타르 아시안컵 호주 VS 대한민국 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg",
            channel = "쿠팡플레이 스포츠",
            time = "2024-02-02T21:30:04Z",
            score = "1",
            favorit = false
        ),
        SeachList(
            title = "[2023 AFC 카타르 아시안컵] 2023 AFC 카타르 아시안컵 호주 VS 대한민국 풀 하이라이트",
            thumbnail = "https://i.ytimg.com/vi/kW_z-NMuZIU/mqdefault.jpg",
            channel = "쿠팡플레이 스포츠",
            time = "2024-02-02T21:30:04Z",
            score = "1",
            favorit = false
        )
    )
    private fun hideSoftKeyBoard() {
        val inputMethodManager =
            view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}