package com.example.meohaji.fragment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import com.example.meohaji.R
import com.example.meohaji.databinding.FragmentMyPageBinding
import com.example.meohaji.databinding.FragmentSearchBinding
import java.nio.file.Files.find

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private var backPressedOnce = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_search, container, false)
        binding = FragmentMyPageBinding.inflate(inflater, container, false)

        overrideBackAction()

        val textView = binding.tvSaved
        val spannableString = SpannableString(textView.text)
//        spannableString.setSpan(ForegroundColorSpan(Color.YELLOW), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.yellow_background)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEditName.setOnClickListener {
//            val dialog = Dialog(context).apply { setContentView(R.layout) }
            /** 프로필 사진, 사용자 이름 추가 기능 위치
             ** 다이얼로그 생성하고 사용하기 커스텀 or 기본*/
        }
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