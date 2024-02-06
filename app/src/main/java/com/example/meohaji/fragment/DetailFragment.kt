package com.example.meohaji.fragment

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.meohaji.MainActivity
import com.example.meohaji.R
import com.example.meohaji.databinding.FragmentDetailBinding
import kotlinx.parcelize.Parcelize
import java.text.DecimalFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


private const val ARG_PARAM1 = "param1"

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var param1: DummyDetail? = null

    private lateinit var homeFragment:HomeFragment
    private lateinit var mainActivity: MainActivity

    private val df = DecimalFormat("#,###")
    private val cnt = DecimalFormat("#.#")
    private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFragment = HomeFragment()
        mainActivity = context as MainActivity

        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
            Log.i("This is DetailFragment","onCreate/arguments : $param1")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.i("This is DetailFragment","onViewCreated : $param1")
        binding.apply {
            tvDetailVideoTitle.text = param1?.title
            Glide.with(mainActivity)
                .load(param1?.thumbnail)
                .into(ivDetailVideoThumbnail)
            tvDetailCountLike.text = df.format(param1?.like)
            tvDetailCountView.text = df.format(param1?.view)
            tvDetailCountRec.text = cnt.format(param1?.recommend)
            tvDetailUploadDate.text = "게시일 : ${dtf.format(OffsetDateTime.parse(param1?.date))}"
            tvDetailTextDescription.text = param1?.description
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: DummyDetail) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
                Log.i("This is DetailFragment","Companion Object : $param1")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * 어댑터와 데이터 통신하기 위한 인터페이스
 */
interface GoDetail {
    fun sendData(v: View, dummy: DummyDetail)
}

/**
 * @property DummyDetail Parcelable 가능한 DummyDetail Data Class
 */
@Parcelize
data class DummyDetail(
    val title: String,
    val thumbnail: String,
    val like: Int,
    val view: Int,
    val recommend: Int?,
    val date: String,
    val description: String?
) : Parcelable