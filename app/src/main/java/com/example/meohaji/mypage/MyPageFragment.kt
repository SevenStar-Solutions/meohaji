package com.example.meohaji.mypage

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meohaji.R
import com.example.meohaji.Utils
import com.example.meohaji.databinding.FragmentMyPageBinding
import com.example.meohaji.detail.BtnClick
import com.example.meohaji.detail.DetailFragment
import com.example.meohaji.detail.DetailTags
import com.example.meohaji.home.VideoForUi
import com.google.gson.GsonBuilder
import de.hdodenhof.circleimageview.CircleImageView


class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private var backPressedOnce = false
    private var selectedImageUri: Uri? = null
    private lateinit var dialogImg: ImageView
    private lateinit var myPageAdapter: MyPageAdapter
    private var items: ArrayList<VideoForUi> = ArrayList()
    private lateinit var preferences: SharedPreferences
    var uiData: List<MyPageUiData> = listOf()

    private val pickImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = uri
                dialogImg.setImageURI(selectedImageUri)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        overrideBackAction()

        preferences = requireContext().getSharedPreferences(
            DetailTags.PREF_KEY,
            Context.MODE_PRIVATE
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val (name, image) = Utils.getMyInfo(requireContext())
        items = loadData()
        selectedImageUri = image?.toUri()
        uiData = listOf(
            MyPageUiData.Profile(name, image),
            MyPageUiData.Title,
        ) + items.map { MyPageUiData.Video(it) }

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
                        uiData = listOf(MyPageUiData.Profile(dialogName.text.toString(), selectedImageUri.toString())) + uiData.subList(1, uiData.size)
                        myPageAdapter.submitList(uiData.toList())
                        Utils.saveMaInfo(requireContext(), dialogName.text.toString(), selectedImageUri.toString())
                        dialog.dismiss()
                    }
                    builder.setNegativeButton("취소") { dialog, _ ->
                        dialog.cancel()
                    }
                    val dialog = builder.create()
                    dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.apply_corner_radius_10))
                    dialog.show()
                    dialogName.setText(name)
//                    dialogImg.setImageDrawable(image)
                    if (image == null) {
                        dialogImg.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_default_profile))
                    } else {
                        dialogImg.setImageDrawable(image)
                    }

                    changeBtn.setOnClickListener {
                        pickImageFromGallery.launch("image/*")
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
        myPageAdapter.submitList(uiData.toList())
    }

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

    fun checkSharedPreference() {
        items = loadData()
        uiData = uiData.subList(0, 2) + items.map { MyPageUiData.Video(it) }
        myPageAdapter.submitList(uiData.toList())
    }

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
