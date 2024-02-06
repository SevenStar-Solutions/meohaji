package com.example.meohaji.fragment

import android.app.AlertDialog
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
import de.hdodenhof.circleimageview.CircleImageView
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
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_mydata, null)
            val profileImg = dialogView.findViewById<CircleImageView>(R.id.civ_dialog_profile)

//            dialogView.background =
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)
            builder.setTitle("커스텀 다이얼로그")
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setPositiveButton("확인") { dialog, which ->
                // 확인 버튼을 클릭했을 때의 동작
            }
            builder.setNegativeButton("취소") { dialog, which ->
                // 취소 버튼을 클릭했을 때의 동작
            }

            val dialog = builder.create()
            dialog.show()


            /*
            val builder = AlertDialog.Builder(this)
            builder.setTitle("커스텀 다이얼로그")
            builder.setIcon(R.mipmap.ic_launcher)

            val v1 = layoutInflater.inflate(R.layout.dialog, null)
            builder.setView(v1)

            // p0에 해당 AlertDialog가 들어온다. findViewById를 통해 view를 가져와서 사용
            val listener = DialogInterface.OnClickListener { p0, p1 ->
                val alert = p0 as AlertDialog
                val edit1: EditText? = alert.findViewById<EditText>(R.id.editText)
                val edit2: EditText? = alert.findViewById<EditText>(R.id.editText2)

                binding.tvTitle.text = "이름 : ${edit1?.text}"
                binding.tvTitle.append(" : 나이 : ${edit2?.text}")
            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", null)
            */


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