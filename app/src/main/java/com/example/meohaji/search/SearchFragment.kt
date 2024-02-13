package com.example.meohaji.search

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meohaji.BuildConfig
import com.example.meohaji.Constants
import com.example.meohaji.NetworkClient
import com.example.meohaji.databinding.FragmentSearchBinding
import com.example.meohaji.detail.DetailFragment
import com.example.meohaji.home.VideoForUi
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

class SearchFragment : Fragment() {

    private lateinit var preferences: SharedPreferences
    private var historyDataList: MutableList<HistoryList> = mutableListOf()


    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var backPressedOnce = false
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter()
    }
    private val historyAdapter: HistoryAdapter by lazy {
        HistoryAdapter()
    }

    /** MVVM은 안쓰면 LiveData는 안써도 무방*/
    private val _searchVideoList = MutableLiveData<List<SearchList>>()
    private val searchVideoList: LiveData<List<SearchList>> get() = _searchVideoList

    private lateinit var mContext: Context
    private lateinit var idData: VideoForUi

    private var pageToken: String? = null
    private var isLoading = false

    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        preferences = requireContext().getSharedPreferences(
            Constants.PREF_KEY,
            Context.MODE_PRIVATE
        )


        overrideBackAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSearch.adapter = searchAdapter
        binding.rvSearchLatestWords.adapter = historyAdapter
        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                val totalItemCount = recyclerView.adapter?.itemCount?.minus(1)
                if (lastItemPosition + 2 == totalItemCount && !isLoading) {
                    isLoading = true
                    communicateSearchVideos()
                }
            }
        })
/**포커스 유무 확인*/
        binding.etSearchFragmentSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.constraintLayoutSearchLatestWords.visibility = if (hasFocus) {
                View.VISIBLE

            } else {
                View.GONE
            }
        }

        //LiveData로 Adapter에 연결할때
        /**viewLifecycleOwner는 화면이 보일때만 감지한다, 옵저브는 데이터가 바뀌는지 지켜보고있음*/
        searchVideoList.observe(viewLifecycleOwner) {
            searchAdapter.submitList(it.toList()) //값
            isLoading = false
        }
        historyAdapter.submitList(historyDataList)

        /**키보드에서 엔터로 검색시작*/
        binding.etSearchFragmentSearch.setOnEditorActionListener { textView, action, event ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                pageToken = null
                binding.etSearchFragmentSearch.clearFocus()
                //키보드 숨기기
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etSearchFragmentSearch.windowToken, 0)
//                saveData(historyDataList)
                communicateSearchVideos()
                handled = true
            }
            handled
        }
        /**클릭이벤트 VideoID로 API를 받아오고 DetailFragment로 데이터 보내고 실행*/
        searchAdapter.videoClick = object : SearchAdapter.SearchVideoClick {
            override fun onClick(videoData: SearchList) {
                communicateIDSearchVideos(videoData.videoId)
            }
        }
    }
/** 최근검색어 */
    private fun saveData(historyData: MutableList<HistoryList>) {
        val editor = preferences.edit()
        val gson = GsonBuilder().create()
        editor.putString("historyData", gson.toJson(historyData))
        editor.apply()
    }

    private fun deleteData(id: String) {
        val editor = preferences.edit()
        editor.remove(id)
        editor.apply()
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    /** 프레그먼트 띄우는 함수 */
    private fun setDetailFragment(item: VideoForUi) {
        val dialog = DetailFragment.newInstance(item)
        dialog.show(requireActivity().supportFragmentManager, "DetailFragment")
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

    /** 클릭한 아이템 id로 데이터 요청*/
    private suspend fun searchVideoById(id: String) = withContext(Dispatchers.IO) {
        NetworkClient.apiService.searchByIdList(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet,statistics",
            id,
        )
    }

    /** List로 넣는 작업*/
    private fun communicateIDSearchVideos(id: String) {
        lifecycleScope.launch {
            runCatching {
                /**런캐칭이 뭔지 검색해보기*/
                val videos = searchVideoById(id = id)
                videos.items[0].let { item ->
                    idData = VideoForUi(
                        item.id,
                        item.snippet.publishedAt,
                        item.snippet.channelTitle,
                        item.snippet.title,
                        item.snippet.description,
                        item.snippet.thumbnails.medium.url,
                        item.statistics.viewCount.toInt(),
                        item.statistics.likeCount?.toInt() ?: 0,
                        item.statistics.commentCount.toInt(),
                        calRecommendScore(
                            item.snippet.description,
                            item.statistics.viewCount.toInt(),
                            item.statistics.likeCount?.toInt() ?: 0,
                            item.statistics.commentCount.toInt()
                        )
                    )
                }
            }.onFailure { //오류가 났을때 실행
                Log.e("search", "잘못됐다")
            }
            setDetailFragment(idData)
        }

    }

    private suspend fun searchByQueryList(query: String, page: String?) =
        withContext(Dispatchers.IO) {
            NetworkClient.apiService.searchByQueryList(
                BuildConfig.YOUTUBE_API_KEY,
                "snippet",
                10,
                "relevance",
                query,
                "kr",
                "video",
                page
            )
        }

    //받아온 데이터를 LiveData에 넣는과정
    private fun communicateSearchVideos() {
        lifecycleScope.launch {
            runCatching {
                /**런캐칭이 뭔지 검색해보기*/
                val search = binding.etSearchFragmentSearch.text.toString()
                val videos = searchByQueryList(query = search, page = pageToken)
                historyDataList.add(HistoryList(search)) //검색어 리스트에 저장
                Log.d("history","검색어 리스트에 저장 $historyDataList")
                /**라이브데이타를 쓸때 .value를 붙여야 변수를 넣는다*/
                /**라이브데이타를 안할땐 searchadapter에 submitlist를 바로 해도된다*/
                if (pageToken == null) {
                    _searchVideoList.value = videos.items.map { item ->
                        SearchList(
                            item.snippet.title,
                            item.snippet.thumbnails.medium.url,
                            item.snippet.channelTitle,
                            outputFormat.format(inputFormat.parse(item.snippet.publishedAt) as Date),
                            item.id.videoId
                        )
                    }
                } else {
                    _searchVideoList.value = searchVideoList.value.orEmpty().toMutableList().apply {
                        addAll(videos.items.map { item ->
                            SearchList(
                                item.snippet.title,
                                item.snippet.thumbnails.medium.url,
                                item.snippet.channelTitle,
                                outputFormat.format(inputFormat.parse(item.snippet.publishedAt) as Date),
                                item.id.videoId
                            )
                        })
                    }
                }
                pageToken = videos.nextPageToken
            }.onFailure { //오류가 났을때 실행
                Log.e("search", "잘못됐다")
            }
        }
    }

    private fun calRecommendScore(
        description: String,
        viewCount: Int,
        likeCount: Int,
        commentCount: Int
    ): Double {
        val viewScore = viewCount * 0.5 * (1.0 / viewCount.toString().length)
        val likeScore = likeCount * 0.3 * (1.0 / likeCount.toString().length)
        val commentScore = commentCount * 0.2 * (1.0 / commentCount.toString().length)
        val isShorts = description.contains("shorts")

        var totalScore =
            if (isShorts) (viewScore + likeScore + commentScore) / viewScore * 3.3 * 1.5 else (viewScore + likeScore + commentScore) / viewScore * 3.3
        totalScore = round(totalScore * 10) / 10
        return if (totalScore > 5.0) 5.0 else totalScore
    }
}