package com.example.meohaji.detail

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.meohaji.R
import com.example.meohaji.databinding.FragmentDetailBinding
import com.example.meohaji.detail.DetailTags.PREF_KEY
import com.example.meohaji.home.HomeFragment
import com.example.meohaji.home.VideoForUi
import com.example.meohaji.main.MainActivity
import com.google.gson.GsonBuilder
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

    private lateinit var homeFragment: HomeFragment
    private lateinit var mainActivity: MainActivity

    private lateinit var preferences: SharedPreferences
    var btnClick: BtnClick? = null

    private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFragment = HomeFragment()
        mainActivity = context as MainActivity
        isCancelable = true
        preferences = requireContext().getSharedPreferences(PREF_KEY, MODE_PRIVATE)

        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
        }
    }

    // 다이얼로그에 스타일 적용
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.DetailTransparent)
        return dialog
    }

    // dialog의 사이즈를 직접 지정하는 부분
//    override fun onResume() {
//        super.onResume()
//        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val rect = windowManager.currentWindowMetrics.bounds
//        val window = dialog?.window
//        val x = (rect.width() * 0.9f).toInt()
//        val y = (rect.height() * 0.8f).toInt()
//        window?.setLayout(x, y)
//        Log.i("This is DetailFragment","onResume : x:$x, y:$y")
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadData()
        Log.i("This is DetailFragment","onViewCreated : ${loadData()}")
        when(param1!!.id) {
            !in preferences.all.keys -> saveButton()
            in preferences.all.keys -> deleteButton()
        }
        p1()

        binding.ivBtnDetailClose.setOnClickListener {       // X버튼 클릭 시 프래그먼트 닫기
            this.dismiss()
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

    private fun p1(){
        binding.apply {
            tvDetailVideoTitle.text = param1?.title
            Glide.with(mainActivity)
                .load(param1?.thumbnail)
                .into(ivDetailVideoThumbnail)
            tvDetailCountLike.text = setCount(param1?.likeCount!!.toLong())
            tvDetailCountView.text = setCount(param1?.viewCount!!.toLong())
            tvDetailCountRec.text = "${param1?.recommendScore}/5.0"
            tvDetailUploadDate.text = "게시일 : ${dtf.format(OffsetDateTime.parse(param1?.publishedAt))}"
            tvDetailTextDescription.text = when(param1?.description) {
                "" -> "내용이 없습니다."
                else -> param1?.description
            }

            btnDetailSaveData.setOnClickListener{
                when(param1!!.id) {
                    !in preferences.all.keys -> {
                        saveData (param1!!)
                        deleteButton()
                    }
                    in preferences.all.keys -> {
                        deleteData(param1!!.id)
                        saveButton()
                    }
                }
                btnClick?.click()
            }
            btnDetailShare.setOnClickListener {
                shareLink(param1!!)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCount(count: Long):String {
        var ans = ""
        if(count / 10000L <= 0L) {
            ans = "$count"
        } else if(count / 10000L > 0L) {
            ans = if((count / 10000L) / 10000L <= 0L) {
                "${count/10000L}.${(count%10000L).toString().first()}만"
            } else {
                "${(count/10000L)/10000L}.${((count/10000L)%10000L).toString().first()}억"
            }
        }
        return ans
    }

    // SharedPreference에 저장(key = id, value = 값.toString)
    private fun saveData(test:VideoForUi) {
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

    // SharedPreferences에서 데이터 불러오기
    private fun loadData():ArrayList<VideoForUi> {
        val allEntries: Map<String, *> = preferences.all
        val bookmarks = ArrayList<VideoForUi>()
        val gson = GsonBuilder().create()
        for((key, value) in allEntries) {
            val item = gson.fromJson(value as String, VideoForUi::class.java)
            bookmarks.add(item)
        }
        return bookmarks
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

    private fun shareLink(data:VideoForUi) {       // 수정 필요
        // 전송 인텐트 생성
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"

        // 링크 생성 & 인텐트에 담기
        val url = "https://youtube.com/video/${data.id}"
        intent.putExtra(Intent.EXTRA_TEXT, url)

        // chooser로 앱 선택하기
        val text = "공유하기"
        startActivity(Intent.createChooser(intent, text))

        // 클립보드 서비스 사용
//        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        val clip: ClipData = ClipData.newPlainText("VideoUri",url)
//        clipboard.setPrimaryClip(clip)
//        toast("클립보드에 복사함.")
    }
}

object DetailTags{
    const val PREF_KEY = "My Preferences"
}