package com.example.meohaji.detail

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.meohaji.R
import com.example.meohaji.databinding.FragmentDetailBinding
import com.example.meohaji.home.VideoForUi
import com.example.meohaji.main.MainActivity
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private const val ARG_PARAM1 = "param1"

interface BtnClick {
    fun click()
}

class DetailFragment : DialogFragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var param1: VideoForUi? = null

    private lateinit var mainActivity: MainActivity
    var btnClick: BtnClick? = null

    private val detailViewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(requireContext())
    }

    private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = context as MainActivity
        isCancelable = true

        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1, VideoForUi::class.java)
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
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        detailViewModel.setUiData(param1)
        detailViewModel.setButton()

        initView()
        initViewModel()
    }

    private fun initView() {
        binding.ivBtnDetailClose.setOnClickListener {       // X버튼 클릭 시 프래그먼트 닫기
            this.dismiss()
        }

        binding.btnDetailSaveData.setOnClickListener{
            detailViewModel.clickSaveOrDelete()
            btnClick?.click()
        }
        binding.btnDetailShare.setOnClickListener {
            detailViewModel.clickShare()
        }
    }

    private fun initViewModel() = with(detailViewModel) {
        isContain.observe(viewLifecycleOwner) {
            if (it) saveButton() else deleteButton()
        }

        isSave.observe(viewLifecycleOwner) {
            if (it) saveButton() else deleteButton()
        }

        uiState.observe(viewLifecycleOwner) {
            with(binding) {
                tvDetailVideoTitle.text = it?.title
                Glide.with(mainActivity)
                    .load(it?.thumbnail)
                    .into(ivDetailVideoThumbnail)
                tvDetailCountLike.text = detailViewModel.setCount(it?.likeCount)
                tvDetailCountView.text = detailViewModel.setCount(it?.viewCount)
                tvDetailCountRec.text = "${it?.recommendScore}/5.0"
                tvDetailUploadDate.text = "게시일 : ${dtf.format(OffsetDateTime.parse(it?.publishedAt))}"
                tvDetailTextDescription.text = when(it?.description) {
                    "" -> "내용이 없습니다."
                    else -> it?.description
                }
            }
        }

        formatUrl.observe(viewLifecycleOwner) {
            // 전송 인텐트 생성
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/html"

            // 링크 생성 & 인텐트에 담기
            intent.putExtra(Intent.EXTRA_TEXT, it)

            // chooser로 앱 선택하기
            val text = "공유하기"
            startActivity(Intent.createChooser(intent, text))
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: VideoForUi) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveButton() = with(binding) {
        btnDetailSaveData.text = "저장"
        btnDetailSaveData.setTextColor(Color.BLACK)
        btnDetailSaveData.setBackgroundResource(R.drawable.apply_detail_button_save)
    }
    private fun deleteButton() = with(binding) {
        btnDetailSaveData.text = "삭제"
        btnDetailSaveData.setTextColor(Color.parseColor("#FF4141"))
        btnDetailSaveData.setBackgroundResource(R.drawable.apply_detail_button_delete)
    }
}

object DetailTags{
    const val PREF_KEY = "My Preferences"
}