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
import com.example.meohaji.R
import com.example.meohaji.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private var backPressedOnce = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_search, container, false)
        binding = FragmentSearchBinding.inflate(inflater, container, false)

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

    private fun hideSoftKeyBoard() {
        val inputMethodManager =
            view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}