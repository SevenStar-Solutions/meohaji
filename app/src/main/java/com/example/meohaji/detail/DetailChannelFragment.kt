package com.example.meohaji.detail

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.meohaji.R
import com.example.meohaji.Utils.setCount
import com.example.meohaji.databinding.FragmentDetailChannelBinding
import com.example.meohaji.home.CategoryChannel
import com.example.meohaji.main.MainActivity

class DetailChannelFragment : DialogFragment() {
    private var _binding: FragmentDetailChannelBinding? = null
    private val binding get() = _binding!!
    private var param1: CategoryChannel? = null

    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = context as MainActivity
        isCancelable = true

        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1, CategoryChannel::class.java)
        }
    }

    // 다이얼로그에 스타일 적용
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.DetailTransparent)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    // 뷰 초기화 함수
    private fun initView() = with(binding) {

        Glide.with(mainActivity)
            .load(param1?.thumbnail)
            .into(civDetailChannelThumbnail)

        tvDetailChannelName.text = param1?.title
        tvDetailChannelSubscriberCount.text = setCount(param1?.subscriberCount?.toLong() ?: 0)
        tvDetailChannelVideoCount.text = setCount(param1?.videoCount?.toLong() ?: 0)
        tvDetailChannelTotalViewCount.text = setCount(param1?.viewCount?.toLong() ?: 0)
        tvDetailChannelTextDescription.text = param1?.description
        tvDetailChannelCustomUrl.text = param1?.customUrl

        // X버튼 클릭 리스너
        ivBtnDetailChannelClose.setOnClickListener {
            dismiss()
        }

        // 썸네일 영역 눌렀을 때 유튜브 앱의 채널로 이동하는 클릭 리스너
        constraintLayoutDetailChannelClickArea.setOnClickListener {
            startActivity(Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/${param1?.customUrl}")
            ).apply {
                setPackage("com.google.android.youtube")
            })
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"

        @JvmStatic
        fun newInstance(param1: CategoryChannel) =
            DetailChannelFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}