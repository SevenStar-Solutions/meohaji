package com.example.meohaji.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meohaji.BuildConfig
import com.example.meohaji.Constants.PREF_RECENT_KEY
import com.example.meohaji.Constants.PREF_RECENT_KEY_VALUE
import com.example.meohaji.NetworkClient
import com.example.meohaji.Utils
import com.example.meohaji.databinding.FragmentSearchBinding
import com.example.meohaji.detail.BtnClick
import com.example.meohaji.detail.DetailFragment
import com.example.meohaji.home.VideoForUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface BtnClick3 {
    fun clickFromSearch()
}

class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var backPressedOnce = false
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter()
    }

    private val searchRecentAdapter: SearchRecentAdapter by lazy {
        SearchRecentAdapter()
    }
    private val _searchRecentList = MutableLiveData<List<RecentDataClass>>()
    private val searchRecentList: LiveData<List<RecentDataClass>> get() = _searchRecentList
    private var etFocus: Boolean = false

    var btnClick3: BtnClick3? = null

    /** MVVM은 안쓰면 LiveData는 안써도 무방*/
    private val _searchVideoList = MutableLiveData<List<SearchList>>()
    private val searchVideoList: LiveData<List<SearchList>> get() = _searchVideoList

    private lateinit var mContext: Context
    private lateinit var idData: VideoForUi

    private var pageToken: String? = null
    private var isLoading = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        if (context is BtnClick3) {
            btnClick3 = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        overrideBackAction()

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSearch.adapter = searchAdapter
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

        // LiveData에 Recent Adapter 연결(???)
        searchRecentList.observe(viewLifecycleOwner) {
            searchRecentAdapter.submitList(showRecentData())
            Log.i("This is SearchFragment","Show Recent : ${showRecentData()}")
        }
        // 최근 기록 어댑터 연결
        binding.rvSearchLatestWords.apply {
            adapter = searchRecentAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        }
        // 어댑터에서 클릭한 아이템의 이름을 받아오는 부분
        searchRecentAdapter.recentData = object : SearchRecentAdapter.RecentData {
            override fun onDeleted(text: String, name: String) {
                deleteRecentData(text)
                searchRecentAdapter.submitList(showRecentData())
                Log.i("This is SearchFragment", "Interface Called : $text, $name")
            }
        }
        searchRecentAdapter.recentClick = object : SearchRecentAdapter.RecentClick {
            override fun onClick(name: String) {
                binding.etSearchFragmentSearch.setText(name)
                binding.etSearchFragmentSearch.clearFocus()
            }
        }

        binding.etSearchFragmentSearch.setOnFocusChangeListener { _, hasFocus ->
            binding.rvSearchLatestWords.scrollToPosition(0)
            binding.constraintLayoutSearchLatestWords.isVisible = hasFocus
            binding.viewSearchClearFocus.isVisible = hasFocus
            _searchRecentList.value = showRecentData()  // 얘가 있어야 LiveData에 업데이트 가능???
        }

        binding.viewSearchClearFocus.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.etSearchFragmentSearch.clearFocus()
                    hideKeyboard()
                    v.isVisible = false
                    true
                }

                else -> false
            }
        }

        //LiveData로 Adapter에 연결할때
        /**viewLifecycleOwner는 화면이 보일때만 감지한다, 옵저브는 데이터가 바뀌는지 지켜보고있음*/
        searchVideoList.observe(viewLifecycleOwner) {
            searchAdapter.submitList(it.toList()) //값
            isLoading = false
        }

        //키보드에서 엔터로 검색시작
        binding.etSearchFragmentSearch.setOnEditorActionListener { textView, action, event ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                pageToken = null
                binding.etSearchFragmentSearch.clearFocus()
                hideKeyboard()
                communicateSearchVideos()
                handled = true

                // 검색어를 최근기록(Shared Preferences)에 저장하는 부분
                if(binding.etSearchFragmentSearch.text.isNotBlank()) {
                    saveRecentData(binding.etSearchFragmentSearch.text.toString())
                }
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

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    /** 프레그먼트 띄우는 함수 */
    private fun setDetailFragment(item: VideoForUi) {
        val dialog = DetailFragment.newInstance(item)
        dialog.btnClick = object : BtnClick {
            override fun click() {
                btnClick3?.clickFromSearch()
            }
        }
        dialog.show(requireActivity().supportFragmentManager, "DetailFragment")
    }

    private fun overrideBackAction() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (binding.etSearchFragmentSearch.hasFocus()) {
                binding.etSearchFragmentSearch.clearFocus()
                return@addCallback
            }

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
                        Utils.calRecommendScore(
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
                /**라이브데이타를 쓸때 .value를 붙여야 변수를 넣는다*/
                /**라이브데이타를 안할땐 searchadapter에 submitlist를 바로 해도된다*/
                if (pageToken == null) {
                    _searchVideoList.value = videos.items.map { item ->
                        SearchList(
                            item.snippet.title,
                            item.snippet.thumbnails.medium.url,
                            item.snippet.channelTitle,
                            Utils.outputFormat.format(Utils.inputFormat.parse(item.snippet.publishedAt) as Date),
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
                                Utils.outputFormat.format(Utils.inputFormat.parse(item.snippet.publishedAt) as Date),
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

    // SharedPreferences에 검색어 저장하기
    private fun saveRecentData(text: String) {
        val preferences = requireContext().getSharedPreferences(PREF_RECENT_KEY, MODE_PRIVATE)
        val editor = preferences.edit()
        val allEntries: Map<String, *> = preferences.all
        // 중복 제거
        allEntries.forEach {
            if(it.value == text) {
                deleteRecentData(it.key)
            }
        }
        editor.putString("${PREF_RECENT_KEY_VALUE}_${dateFormat()}", text)
        editor.apply()
    }

    // SharedPreferences에 있는 검색어 삭제하기
    private fun deleteRecentData(text: String) {
        val preferences = requireContext().getSharedPreferences(PREF_RECENT_KEY, MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove(text)
        editor.apply()
        Log.i("This is SearchFragment","Delete Data : $text")
    }

    // SharedPreferences의 검색어 목록 보여주기(최근 검색어 순)
    private fun showRecentData() : ArrayList<RecentDataClass>{
        val preferences = requireContext().getSharedPreferences(PREF_RECENT_KEY, MODE_PRIVATE)
        val allEntries:Map<String, *> = preferences.all.toSortedMap(compareByDescending { it })
        val recent = ArrayList<RecentDataClass>()
        for((key, value) in allEntries) {
            recent.add(RecentDataClass(value as String, key))
        }
        Log.i("This is SearchFragment","recent : $recent")
        return recent
    }

    private fun dateFormat(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale("ko","KR"))
        return dateFormat.format(date)
    }

    private fun hideKeyboard() {
        //키보드 숨기기
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearchFragmentSearch.windowToken, 0)
    }
}

data class RecentDataClass(
    val text: String,
    val time: String
)