package com.example.meohaji.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meohaji.Utils
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.meohaji.R
import com.example.meohaji.databinding.FragmentMyPageBinding
import com.example.meohaji.databinding.FragmentSearchBinding
import de.hdodenhof.circleimageview.CircleImageView
import java.nio.file.Files.find

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private var backPressedOnce = false
    private var selectedImageUri: Uri? = null
    val pickImageFromGallery =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = uri
//                binding.civProfile.setImageURI(selectedImageUri)
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_search, container, false)
        binding = FragmentMyPageBinding.inflate(inflater, container, false)

        overrideBackAction()

        val textView = binding.tvSaved
        val spannableString = SpannableString(textView.text)
//        spannableString.setSpan(ForegroundColorSpan(Color.YELLOW), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.yellow_background)), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString

        val (name, image) = Utils.getMyInfo(requireContext())
        binding.tvName.text = name
        Glide.with(requireContext())
            .load(image?.toUri())
            .into(binding.civProfile)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEditName.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_mydata, null)
            val dialogName = dialogView.findViewById<EditText>(R.id.et_dialog_name)
            val dialogImg = dialogView.findViewById<CircleImageView>(R.id.civ_dialog_profile)
            val changeBtn = dialogView.findViewById<Button>(R.id.btn_dialog_change)

            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)
            builder.setPositiveButton("확인") { dialog, _ ->
//                Utils.saveMaInfo(requireContext(), binding.tvName.text.toString(),binding.civProfile.drawable.toString())
                binding.tvName.text = dialogName.text
                binding.civProfile.setImageURI(selectedImageUri)
                Utils.saveMaInfo(requireContext(), dialogName.text.toString(), selectedImageUri.toString())
                dialog.dismiss()
            }
            builder.setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }

            val dialog = builder.create()
//            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) //투명화 적용 시 builder 위치 사라짐
            dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.apply_corner_radius_10)
            ) //윈도우 전체에 백그라운드 적용 굿

            //다이얼로그 시작 시 설정
            dialog.show()
            dialogName.setText(binding.tvName.text)
            dialogImg.setImageDrawable(binding.civProfile.drawable)

            //버튼액션
            changeBtn.setOnClickListener {
                // 이미지를 선택하고 결과를 처리하는 코드를 등록
                pickImageFromGallery.launch("image/*")
            }
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