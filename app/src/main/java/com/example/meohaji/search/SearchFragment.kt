package com.example.meohaji.search

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.view.KeyEvent.KEYCODE_ENTER
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.meohaji.BuildConfig
import com.example.meohaji.NetworkClient
import com.example.meohaji.databinding.FragmentSearchBinding
import com.example.meohaji.detail.DetailFragment
import com.example.meohaji.detail.DetailTags.DETAIL_SEARCH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private var backPressedOnce = false
    private val searchAdapter: SearchAdapter by lazy {
        SearchAdapter(requireContext())
    }
/** MVVM은 안쓰면 LiveData는 안써도 무방*/
    private val searchVideoList = arrayListOf<SearchList>()
    private val _searchVideoList = MutableLiveData<List<SearchList>>()
    private val searchVideo : LiveData<List<SearchList>> get()= _searchVideoList

    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.fragment_search, container, false)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

//        binding.etSeachfragmentSeach.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                /** 여기다 검색 기능 추가하시면 됩니다 */
//                hideSoftKeyBoard()
//                return@setOnEditorActionListener true
//            }
//            false
//        }

        overrideBackAction()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSeachRecyclerview.adapter= searchAdapter
//        searchAdapter.submitList(searchVideoList)
        //LiveData로 Adapter에 연결할때
        /**viewLifecycleOwner는 화면이 보일때만 감지한다, 옵저브는 데이터가 바뀌는지 지켜보고있음*/
        searchVideo.observe(viewLifecycleOwner){
            searchAdapter.submitList(it.toList()) //값
        }
        //키보드에서 엔터로 검색시작
        binding.etSeachfragmentSeach.setOnEditorActionListener{ textView, action, event ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                communicateSearchVideos()
                handled = true
            }
            handled

            //키보드 숨기기
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etSeachfragmentSeach.windowToken, 0)
            handled
        }
        searchAdapter.videoClick = object : SearchAdapter.SearchVideoClick {
            override fun onClick(videoData: SearchList) {
                setDetailFragment(videoData, DETAIL_SEARCH)


            }
        }

        /** VideoAdapter에서 interface로 받은 데이터를 DetailFragment Dialog로 띄우는 부분 */
//        mostPopularVideoAdapter.videoClick =
//            object : MostPopularVideoAdapter.MostPopularVideoClick {
//                override fun onClick(videoData: MostPopularVideo) {
//                    setDetailFragment(videoData, DETAIL_MOST)
//                    Log.i("This is HomeFragment", "MostPopularVideoAdapter Interface : $videoData")
//                }
//            }
//        categoryVideoAdapter.videoClick = object : CategoryVideoAdapter.CategoryVideoClick {
//            override fun onClick(videoData: CategoryVideo) {
//                setDetailFragment(videoData, DETAIL_CATEGORY)
//                Log.i("This is HomeFragment", "CategoryVideoAdapter Interface : $videoData")
//            }
//        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    /** 프레그먼트 띄우는 함수 */
    private fun setDetailFragment(item: Parcelable, key: String) {
        val dialog = DetailFragment.newInstance(item,key)
        dialog.show(requireActivity().supportFragmentManager,"DetailFragment")
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

    //API데이터 요청
    private suspend fun searchByQueryList(query: String) = withContext(Dispatchers.IO) {
        NetworkClient.apiService.searchByQueryList(
            BuildConfig.YOUTUBE_API_KEY,
            "snippet",
            10,
            "date",
            query,
            "kr",
            "video"
        )
    }

    //받아온 데이터를 LiveData에 넣는과정
    private fun communicateSearchVideos() {
        lifecycleScope.launch {
            runCatching { /**런캐칭이 뭔지 검색해보기*/
                val search = binding.etSeachfragmentSeach.text.toString()
                val videos = searchByQueryList(query = search)
                searchVideoList.clear()
                videos.items.forEach { item ->
                    searchVideoList.add(
                        SearchList(
                            item.snippet.title,
                            item.snippet.thumbnails.high.url,
                            item.snippet.channelTitle,
                            item.snippet.publishedAt,
                        )
                    )
                }
                _searchVideoList.value = searchVideoList  /**라이브데이타를 쓸때 .value를 붙여야 변수를 넣는다*/
                /**라이브데이타를 안할땐 searchadapter에 submitlist를 바로 해도된다*/
//                searchAdapter.submitList(searchVideoList.toList())
            }.onFailure { //오류가 났을때 실행
                Log.e("search","잘못됐다")
            }
        }
    }

    private fun hideSoftKeyBoard() {
        val inputMethodManager =
            view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}