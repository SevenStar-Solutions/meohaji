package com.example.meohaji.mypage

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.bumptech.glide.Glide
import com.example.meohaji.R
import com.example.meohaji.Utils
import com.example.meohaji.databinding.FragmentMyPageBinding
import com.example.meohaji.detail.DetailTags
import com.example.meohaji.home.VideoForUi
import com.google.gson.GsonBuilder


class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private var backPressedOnce = false
    private var selectedImageUri: Uri? = null
    private lateinit var dialogImg: ImageView
    private lateinit var myPageAdapter: MyPageAdapter
    private var items: ArrayList<VideoForUi> = ArrayList()
    private lateinit var preferences: SharedPreferences

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
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        overrideBackAction()

        preferences = requireContext().getSharedPreferences(
            DetailTags.PREF_KEY,
            Context.MODE_PRIVATE
        )

        val textView = binding.tvMyPageSavedVideo
        val spannableString = SpannableString(textView.text)
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.yellow_background
                )
            ),
            0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.text = spannableString

        val (name, image) = Utils.getMyInfo(requireContext())
        binding.tvMyPageName.text = name
        Glide.with(requireContext())
            .load(image?.toUri())
            .into(binding.civMyPageProfile)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        items = loadData()
        myPageAdapter = MyPageAdapter(requireContext())
        myPageAdapter.submitList(items.toList())
        val recyclerView = binding.rvMyPage
        recyclerView.adapter = myPageAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        binding.btnMyPageEditName.setOnClickListener {
            val dialogView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_mydata, null)
            dialogImg = dialogView.findViewById(R.id.civ_dialog_profile)
            val dialogName = dialogView.findViewById<EditText>(R.id.et_dialog_name)
            val changeBtn = dialogView.findViewById<Button>(R.id.btn_dialog_change)

            val builder = AlertDialog.Builder(context)
            builder.setView(dialogView)
            builder.setPositiveButton("확인") { dialog, _ ->
                binding.tvMyPageName.text = dialogName.text
                binding.civMyPageProfile.setImageURI(selectedImageUri)
                Utils.saveMaInfo(
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
            dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.apply_corner_radius_10))
            dialog.show()
            dialogName.setText(binding.tvMyPageName.text)
            dialogImg.setImageDrawable(binding.civMyPageProfile.drawable)

            changeBtn.setOnClickListener {
                pickImageFromGallery.launch("image/*")
            }
        }

        binding.btnClearSavedVideo.setOnClickListener {
            Utils.deletePrefItem(requireContext())
            items.clear() // 저장된 아이템 리스트를 비웁니다.
            myPageAdapter.submitList(items.toList())
        }
        binding.btnRefreshSavedVideo.setOnClickListener {
            items = loadData()
            myPageAdapter.submitList(items.toList())
        }
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
        myPageAdapter.submitList(items.toList())
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
}
