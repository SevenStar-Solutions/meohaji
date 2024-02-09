package com.example.meohaji.mypage

import android.app.AlertDialog
import android.content.Intent
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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.meohaji.R
import com.example.meohaji.Utils
import com.example.meohaji.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private var backPressedOnce = false
    private var selectedImageUri: Uri? = null
    private lateinit var dialogImg: ImageView
    private val myPageAdapter: MyPageAdapter by lazy {
        MyPageAdapter(requireContext())
    }

    private val pickImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                context?.grantUriPermission("com.example.meohaji", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                selectedImageUri = uri
                dialogImg.setImageURI(selectedImageUri)
            }
        }

    private val myPageViewModel: MyPageViewModel by viewModels {
        MyPageViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)

        overrideBackAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myPageViewModel.loadData()
        myPageViewModel.loadUserData()

        initView()
        initViewModel()
    }

    private fun initView() {
        val spannableString = SpannableString(binding.tvMyPageSavedVideo.text)
        spannableString.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.yellow_background
                )
            ),
            0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvMyPageSavedVideo.text = spannableString

        binding.rvMyPage.adapter = myPageAdapter
        binding.rvMyPage.layoutManager = LinearLayoutManager(requireContext())

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
            myPageViewModel.clearData()
        }
        binding.btnRefreshSavedVideo.setOnClickListener {
            myPageViewModel.loadData()
        }
    }

    private fun initViewModel() = with(myPageViewModel) {
        uiState.observe(viewLifecycleOwner) {
            myPageAdapter.submitList(it.toList())
        }

        userImage.observe(viewLifecycleOwner) {
            Glide.with(requireContext())
                .load(it?.toUri())
                .into(binding.civMyPageProfile)
        }

        userName.observe(viewLifecycleOwner) {
            binding.tvMyPageName.text = it
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
        myPageViewModel.loadData()
    }
}
