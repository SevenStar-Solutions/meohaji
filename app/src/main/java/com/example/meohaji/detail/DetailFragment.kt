package com.example.meohaji.detail

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.meohaji.Constants.PREF_KEY
import com.example.meohaji.R
import com.example.meohaji.Utils
import com.example.meohaji.Utils.setCount
import com.example.meohaji.databinding.FragmentDetailBinding
import com.example.meohaji.detail.DetailTags.YOUTUBE_LINK
import com.example.meohaji.home.VideoForUi
import com.example.meohaji.main.MainActivity
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

interface BtnClick {
    fun click()
}

class DetailFragment : DialogFragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var param1: VideoForUi? = null

    private lateinit var mainActivity: MainActivity

    private lateinit var preferences: SharedPreferences
    var btnClick: BtnClick? = null

    private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = context as MainActivity
        isCancelable = true
        preferences = requireContext().getSharedPreferences(PREF_KEY, MODE_PRIVATE)

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
        initView()
        initChart()
    }

    private fun initView() = with(binding) {
        when (param1!!.id) {
            !in preferences.all.keys -> saveButton()
            in preferences.all.keys -> deleteButton()
        }

        tvDetailVideoTitle.text = param1?.title
        Glide.with(mainActivity)
            .load(param1?.thumbnail)
            .into(ivDetailVideoThumbnail)
        tvDetailCountLike.text = setCount(param1?.likeCount!!.toLong())
        tvDetailCountView.text = setCount(param1?.viewCount!!.toLong())
        tvDetailCountRec.text = "${param1?.recommendScore}/5.0"
        tvDetailUploadDate.text =
            "게시일 : ${dtf.format(OffsetDateTime.parse(param1?.publishedAt))}"
        tvDetailTextDescription.text = when (param1?.description) {
            "" -> "내용이 없습니다."
            else -> param1?.description
        }

            btnDetailSaveData.setOnClickListener {
                when (param1!!.id) {
                    !in preferences.all.keys -> {
                        saveData(param1!!)
                        snackBar(requireView(),"내 보관함에 저장됨.")
                        deleteButton()
                    }

                    in preferences.all.keys -> {
                        deleteData(param1!!.id)
                        snackBar(requireView(),"내 보관함에서 삭제됨.")
                        saveButton()
                    }
                }
                btnClick?.click()
            }
            btnDetailShare.setOnClickListener {
                shareLink(param1!!)
            }

        binding.ivBtnDetailClose.setOnClickListener { // X버튼 클릭 시 프래그먼트 닫기
            this@DetailFragment.dismiss()
        }
    }

    private fun initChart() {
        val average = Utils.getCounts(requireContext())
        val entry1 = arrayListOf<BarEntry>(
            BarEntry(1f, average.first.toFloat() / 5),
            BarEntry(2f, average.second.toFloat() / 100),
            BarEntry(3f, average.third.toFloat()),
        )

        val entry2 = arrayListOf<BarEntry>(
            BarEntry(1f, param1?.likeCount!!.toFloat() / 5),
            BarEntry(2f, param1?.viewCount!!.toFloat() / 100),
            BarEntry(3f, param1?.commentCount!!.toFloat()),
        )

        val dataSet1 = BarDataSet(entry1, "인기 동영상 평균")
        dataSet1.color = ContextCompat.getColor(mainActivity, R.color.yellow_background)

        val dataSet2 = BarDataSet(entry2, "현재 동영상")
        dataSet2.color = ContextCompat.getColor(mainActivity, R.color.white)

        val dataSet: ArrayList<IBarDataSet> = arrayListOf(
            dataSet1,
            dataSet2
        )
        val data = BarData(dataSet)
        data.barWidth = 0.2f

        binding.barChartDetail.run {
            this.data = data
            setFitBars(true)

            description.isEnabled = false
            setMaxVisibleValueCount(3)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawGridBackground(false)
            axisLeft.run {
                axisMinimum = 0f
                setDrawAxisLine(false)
                setDrawLabels(false)
                setDrawGridLines(false)
                axisLineColor = ContextCompat.getColor(mainActivity, R.color.white)
                gridColor = ContextCompat.getColor(mainActivity, R.color.white)
                textColor = ContextCompat.getColor(mainActivity, R.color.white)
                textSize = 13f
            }
            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM
                axisMaximum = (0f + barData.getGroupWidth(0.44f, 0.08f) * 3)
                axisMinimum = 0f
                granularity = 1f
                setDrawAxisLine(true)
                setDrawGridLines(false)
                setCenterAxisLabels(true)
                isGranularityEnabled = true
                textColor = ContextCompat.getColor(mainActivity, R.color.white)
                textSize = 12f
                valueFormatter = MyXAxisFormatter()
            }
            axisRight.isEnabled = false
            setTouchEnabled(false)
            groupBars(0f, 0.44f, 0.08f)
            animateY(1000)
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.textColor = ContextCompat.getColor(mainActivity, R.color.white)
            legend.isEnabled = true
            invalidate()
        }
    }

    inner class MyXAxisFormatter : ValueFormatter() {
        private val counts = arrayOf("좋아요수", "조회수", "댓글수")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return counts.getOrNull(value.toInt()) ?: value.toString()
        }
    }

    // SharedPreference에 저장(key = id, value = 값.toString)
    private fun saveData(test: VideoForUi) {
        val editor = preferences.edit()
        val gson = GsonBuilder().create()
        editor.putString(test.id, gson.toJson(test))
        editor.apply()
    }

    // SharedPreference에서 삭제
    private fun deleteData(id: String) {
        val editor = preferences.edit()
        editor.remove(id)
        editor.apply()
    }

    private fun saveButton() {
        binding.btnDetailSaveData.text = "저장"
        binding.btnDetailSaveData.setTextColor(Color.BLACK)
        binding.btnDetailSaveData.setBackgroundResource(R.drawable.apply_detail_button_save)
    }
    private fun deleteButton() {
        binding.btnDetailSaveData.text = "삭제"
        binding.btnDetailSaveData.setTextColor(Color.parseColor("#FF4141"))
        binding.btnDetailSaveData.setBackgroundResource(R.drawable.apply_detail_button_delete)
    }

    private fun shareLink(data: VideoForUi) {
        // 전송 인텐트 생성
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"

        // 링크 생성 & 인텐트에 담기
        val url = "$YOUTUBE_LINK${data.id}"
        intent.putExtra(Intent.EXTRA_TEXT, url)

        // chooser로 앱 선택하기
        val text = "공유하기"
        startActivity(Intent.createChooser(intent, text))
    }

    companion object {
        private const val ARG_PARAM1 = "param1"

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
}
// 스낵바 생성
private fun snackBar(view: View, str:String) {
    Snackbar.make(view, str, Snackbar.LENGTH_SHORT).show()
}


object DetailTags{
    const val YOUTUBE_LINK = "https://youtube.com/watch?v="
}
