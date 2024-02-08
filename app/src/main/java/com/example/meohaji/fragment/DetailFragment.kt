package com.example.meohaji.fragment

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.meohaji.CategoryVideo
import com.example.meohaji.MainActivity
import com.example.meohaji.MostPopularVideo
import com.example.meohaji.databinding.FragmentDetailBinding
import com.example.meohaji.fragment.DetailTags.DETAIL_CATEGORY
import com.example.meohaji.fragment.DetailTags.DETAIL_MOST
import com.example.meohaji.fragment.DetailTags.PREF_KEY
import com.google.gson.GsonBuilder
import java.text.DecimalFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DetailFragment : DialogFragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var param1: MostPopularVideo? = null
    private var param2: CategoryVideo? = null
    private var keyString: String? = null

    private lateinit var homeFragment:HomeFragment
    private lateinit var mainActivity: MainActivity

    private val df = DecimalFormat("#,###")
    private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFragment = HomeFragment()
        mainActivity = context as MainActivity
        isCancelable = true

        arguments?.let {
            Log.i("This is DetailFragment","onCreate/keyString : $keyString")
            when(keyString) {
                DETAIL_MOST -> param1 = it.getParcelable(ARG_PARAM1)
                DETAIL_CATEGORY -> param2 = it.getParcelable(ARG_PARAM2)
            }
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
        when(keyString) {
            DETAIL_MOST -> p1()
            DETAIL_CATEGORY -> p2()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Parcelable, key:String) =
            DetailFragment().apply {
                when(key) {
                    DETAIL_MOST -> {
                        arguments = Bundle().apply {
                            putParcelable(ARG_PARAM1, param1)
                            keyString = key
                        }
                    }
                    DETAIL_CATEGORY -> {
                        arguments = Bundle().apply {
                            putParcelable(ARG_PARAM2, param1)
                            keyString = key
                        }
                    }
                }
                Log.i("This is DetailFragment","Companion Object : $param1")
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
                saveData(param1!!)
                Toast.makeText(mainActivity,"saved! : ${param1?.title}", Toast.LENGTH_SHORT).show()
            }
            btnDetailShare.setOnClickListener {
                Toast.makeText(mainActivity,"share! : ${param1?.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun p2(){
        binding.apply {
            tvDetailVideoTitle.text = param2?.title
            Glide.with(mainActivity)
                .load(param2?.thumbnail)
                .into(ivDetailVideoThumbnail)
            tvDetailCountLike.text = setCount(param2?.likeCount!!.toLong())
            tvDetailCountView.text = setCount(param2?.viewCount!!.toLong())
            tvDetailCountRec.text = "${param2?.recommendScore}/5.0"
            tvDetailUploadDate.text = "게시일 : ${dtf.format(OffsetDateTime.parse(param2?.publishedAt))}"
            tvDetailTextDescription.text = when(param2?.description) {
                "" -> "내용이 없습니다."
                else -> param2?.description
            }

            btnDetailSaveData.setOnClickListener{
                saveData(param2!!)
                Toast.makeText(mainActivity,"saved! : ${param2?.title}", Toast.LENGTH_SHORT).show()
            }
            btnDetailShare.setOnClickListener {
                Toast.makeText(mainActivity,"share! : ${param2?.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        keyString = null
    }

    private fun setCount(count: Long):String {
        var ans = ""
        Log.i("This is DetailFragment","setCount : $count")
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

    private fun saveData(test:Parcelable) {
        val preferences = requireContext().getSharedPreferences(PREF_KEY, MODE_PRIVATE)
        val editor = preferences.edit()
        val gson = GsonBuilder().create()
        when(test) {
            is MostPopularVideo -> {
                editor.putString((test as MostPopularVideo).id, gson.toJson((test as MostPopularVideo)))
            }
            is CategoryVideo -> {
                editor.putString((test as CategoryVideo).id, gson.toJson((test as CategoryVideo)))
            }
        }
        editor.apply()
    }
}

object DetailTags{
    const val DETAIL_MOST = "MostPopular"
    const val DETAIL_CATEGORY = "Category"
    const val PREF_KEY = "My Preferences"
}