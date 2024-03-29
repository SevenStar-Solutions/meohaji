package com.example.meohaji.mypage

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meohaji.Constants.PREF_KEY
import com.example.meohaji.NetworkCheckActivity
import com.example.meohaji.NetworkStatus
import com.example.meohaji.R
import com.example.meohaji.Utils
import com.example.meohaji.databinding.FragmentMyPageBinding
import com.example.meohaji.detail.BtnClick
import com.example.meohaji.detail.DetailFragment
import com.example.meohaji.home.VideoForUi
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.GsonBuilder

class MyPageFragment : Fragment() {

    companion object {
        fun newInstance() = MyPageFragment()
    }

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private var backPressedOnce = false
    private var selectedImageUri: Uri? = null
    private lateinit var dialogImg: ImageView
    private lateinit var myPageAdapter: MyPageAdapter
    private var items: ArrayList<VideoForUi> = ArrayList()
    private lateinit var preferences: SharedPreferences
    var uiData: List<MyPageUiData> = listOf()

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            //ImagePicker로 이미지를 성공적으로 받아오면 selectedImageUri과 dialogImg에 전달
            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data!!

                selectedImageUri = fileUri
                dialogImg.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "선택을 취소하셨습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 페이드인 애니메이션
    private val fadeIn: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
    }

    // 페이드아웃 애니메이션
    private val fadeOut: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        overrideBackAction()

        preferences = requireContext().getSharedPreferences(
            PREF_KEY,
            Context.MODE_PRIVATE
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (name, image) = Utils.getMyInfo(requireContext())
        items = loadData()
        selectedImageUri = image?.toUri()
        //표시할 내용이 없는 경우 텍스트뷰로 알림
        uiData = if (items.isEmpty()) {
            listOf(
                MyPageUiData.Profile(name, image),
                MyPageUiData.Title,
                MyPageUiData.Text
            )
        } else {
            listOf(
                MyPageUiData.Profile(name, image),
                MyPageUiData.Title,
            ) + items.map { MyPageUiData.Video(it) }
        }

        //프로필 수정 다이얼로그
        myPageAdapter = MyPageAdapter(requireContext())
        myPageAdapter.apply {
            editMyProfile = object : MyPageAdapter.EditMyProfile {
                override fun open(name: String, image: Drawable?) {
                    val dialogView =
                        LayoutInflater.from(requireContext()).inflate(R.layout.dialog_mydata, null)
                    dialogImg = dialogView.findViewById(R.id.civ_dialog_profile)
                    val dialogName = dialogView.findViewById<EditText>(R.id.et_dialog_name)
                    val changeBtn = dialogView.findViewById<Button>(R.id.btn_dialog_change)
                    val builder = AlertDialog.Builder(context)
                    builder.setView(dialogView)
                    builder.setPositiveButton("확인") { dialog, _ ->
                        uiData = listOf(
                            MyPageUiData.Profile(
                                dialogName.text.toString(),
                                selectedImageUri.toString()
                            )
                        ) + uiData.subList(1, uiData.size)
                        myPageAdapter.submitList(uiData.toList())
                        Utils.saveMyInfo(
                            requireContext(),
                            dialogName.text.toString(),
                            selectedImageUri.toString()
                        )
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("취소") { dialog, _ ->
                        dialog.cancel()
                    }
                    val dialog = builder.create()
                    dialog.window?.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.apply_corner_radius_10
                        )
                    )
                    dialog.show()
                    dialogName.setText(name)
                    if (image == null) {
                        dialogImg.setImageDrawable(
                            ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_default_profile
                            )
                        )
                    } else {
                        dialogImg.setImageDrawable(image)
                    }
                    changeBtn.setOnClickListener {
                        ImagePicker.with(requireActivity())
                            .galleryOnly()
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .cropSquare()
                            .createIntent { intent ->
                                startForProfileImageResult.launch(intent)
                            }
                    }
                }
            }
            detailSaveVideo = object : MyPageAdapter.DetailSaveVideo {
                override fun move(videoData: VideoForUi) {
                    setDetailFragment(videoData)
                }
            }
        }
        binding.rvMyPage.adapter = myPageAdapter
        binding.rvMyPage.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyPage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var curVisible = false
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.canScrollVertically(-1)
                        .not()
                ) {
                    binding.fabMyPageScrollUp.startAnimation(fadeOut)
                    binding.fabMyPageScrollUp.isVisible = false
                    curVisible = false
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    binding.fabMyPageScrollUp.isVisible = true
                    if (curVisible.not()) binding.fabMyPageScrollUp.startAnimation(fadeIn)
                    curVisible = true
                }
            }
        })

        binding.fabMyPageScrollUp.setOnClickListener {
            binding.rvMyPage.smoothScrollToPosition(0)
        }

        myPageAdapter.submitList(uiData.toList())
    }

    //뒤로가기 클릭 시 한번 더 눌러서 종료
    private fun overrideBackAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (backPressedOnce) {
                requireActivity().finish()
            } else {
                backPressedOnce = true
                Toast.makeText(requireContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    backPressedOnce = false
                }, 2000)
            }
        }
    }

    //SharedPreference 저장분 확인
    fun checkSharedPreference() {
        items = loadData()
        uiData = if (items.isEmpty()) {
            uiData.subList(0, 2) + MyPageUiData.Text
        } else {
            uiData.subList(0, 2) + items.map { MyPageUiData.Video(it) }
        }
        myPageAdapter.submitList(uiData.toList())
    }

    //저장한 이미지 불러오기
    private fun loadData(): ArrayList<VideoForUi> {
        val allEntries: Map<String, *> = preferences.all
        val bookmarks = ArrayList<VideoForUi>()
        val gson = GsonBuilder().create()
        for ((key, value) in allEntries) {
            val item = gson.fromJson(value as String, VideoForUi::class.java)
            bookmarks.add(item)
        }
        return bookmarks
    }

    //디테일 프레그먼트의 저장 변경 신호를 가져오기
    private fun setDetailFragment(item: VideoForUi) {
        val dialog = DetailFragment.newInstance(item)
        dialog.btnClick = object : BtnClick {
            override fun click() {
                checkSharedPreference()
            }
        }
        dialog.show(requireActivity().supportFragmentManager, "DetailFragment")
    }
}
