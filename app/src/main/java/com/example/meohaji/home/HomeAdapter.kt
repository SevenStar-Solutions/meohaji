package com.example.meohaji.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.Utils
import com.example.meohaji.databinding.ItemVideoByCategoryBinding
import com.example.meohaji.databinding.LayoutChannelByCategoryBinding
import com.example.meohaji.databinding.LayoutMostPopularVideoBinding
import com.example.meohaji.databinding.LayoutSelectCategoryBinding
import com.example.meohaji.databinding.LayoutThemeTitleBinding
import com.example.meohaji.databinding.LayoutThemeTitleWithSpinnerBinding
import com.google.android.material.R
import java.util.Date

class HomeAdapter(private val context: Context) :
    ListAdapter<HomeUiData, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<HomeUiData>() {
        override fun areItemsTheSame(oldItem: HomeUiData, newItem: HomeUiData): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: HomeUiData, newItem: HomeUiData): Boolean {
            return oldItem == newItem
        }
    }) {

    // 카테고리 지정에 사용되는 인터페이스
    interface CommunicateVideoByCategory {
        fun call(id: String, sortOrder: Int)
    }

    // 가장 인기있는 영상 상세 페이지 이동에 사용되는 인터페이스
    interface DetailMostPopularVideo {
        fun move(videoData: VideoForUi)
    }

    // 카테고리 영상 상세 페이지 이동에 사용되는 인터페이스
    interface DetailCategoryVideo {
        fun move(videoData: VideoForUi)
    }

    // 카테고리 채널 상세 페이지 이동에 사용되는 인터페이스
    interface DetailCategoryChannel {
        fun move(channelData: CategoryChannel)
    }

    // 카테고리 영상 정렬에 사용되는 인터페이스
    interface SortCategoryVideo {
        fun sort(order: Int)
    }

    var communicateVideoByCategory: CommunicateVideoByCategory? = null
    var detailMostPopularVideo: DetailMostPopularVideo? = null
    var detailCategoryVideo: DetailCategoryVideo? = null
    var detailCategoryChannel: DetailCategoryChannel? = null
    var sortCategoryVideo: SortCategoryVideo? = null

    // 카테고리 위치 저장하는 변수
    private var categorySpinnerIdx = 0

    // 스피너 위치 저장하는 변수
    private var sortSpinnerIdx = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHANNEL -> {
                CategoryChannelViewHolder(
                    LayoutChannelByCategoryBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            VIDEO -> {
                CategoryVideoViewHolder(
                    ItemVideoByCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            POPULARVIDEO -> {
                MostPopularVideoViewHolder(
                    LayoutMostPopularVideoBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            SPINNER -> {
                SpinnerViewHolder(
                    LayoutSelectCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            TITLE -> {
                TitleViewHolder(
                    LayoutThemeTitleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                TitleWithViewHolder(
                    LayoutThemeTitleWithSpinnerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position)) {
            is HomeUiData.Title -> (holder as TitleViewHolder).bind(currentList[position] as HomeUiData.Title)
            is HomeUiData.CategoryChannels -> (holder as CategoryChannelViewHolder).bind(currentList[position] as HomeUiData.CategoryChannels)
            is HomeUiData.CategoryVideos -> (holder as CategoryVideoViewHolder).bind(currentList[position] as HomeUiData.CategoryVideos)
            is HomeUiData.MostPopularVideos -> (holder as MostPopularVideoViewHolder).bind(
                currentList[position] as HomeUiData.MostPopularVideos
            )

            is HomeUiData.Spinner -> (holder as SpinnerViewHolder).bind(currentList[position] as HomeUiData.Spinner)
            else -> (holder as TitleWithViewHolder).bind(currentList[position] as HomeUiData.TitleWithSpinner)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HomeUiData.CategoryChannels -> CHANNEL
            is HomeUiData.CategoryVideos -> VIDEO
            is HomeUiData.MostPopularVideos -> POPULARVIDEO
            is HomeUiData.Spinner -> SPINNER
            is HomeUiData.Title -> TITLE
            else -> TITLE_SPINNER
        }
    }

    // 카테고리 채널 영역 뷰홀더
    inner class CategoryChannelViewHolder(private val binding: LayoutChannelByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.CategoryChannels) {

            val categoryChannelAdapter = CategoryChannelAdapter(context)
            binding.rvHomeChannelByCategory.adapter = categoryChannelAdapter
            categoryChannelAdapter.submitList(item.list.toList())
            // 채널 클릭 이벤트
            categoryChannelAdapter.videoChannelClick =
                object : CategoryChannelAdapter.VideoChannelClick {
                    override fun onClick(channelData: CategoryChannel) {
                        detailCategoryChannel?.move(channelData)
                    }
                }
        }
    }

    // 카테고리 영상 영역 뷰홀더
    inner class CategoryVideoViewHolder(private val binding: ItemVideoByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.CategoryVideos) = with(binding) {

            Glide.with(context)
                .load(item.video.thumbnail)
                .into(ivVideoByCategoryItemThumbnail)

            tvVideoByCategoryItemTitle.text = item.video.title
            tvVideoByCategoryItemChannelName.text = item.video.channelTitle
            tvVideoByCategoryItemUploadDate.text =
                Utils.outputFormat.format(Utils.inputFormat.parse(item.video.publishedAt) as Date)
            tvVideoByCategoryItemRecommendScore.text = item.video.recommendScore.toString()
            // 영상 클릭 이벤트
            ivVideoByCategoryItemThumbnail.setOnClickListener {
                detailCategoryVideo?.move(item.video)
            }
        }
    }

    // 가장 인기있는 영상 영역 뷰홀더
    inner class MostPopularVideoViewHolder(private val binding: LayoutMostPopularVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.MostPopularVideos) {

            val mostPopularVideoAdapter = MostPopularVideoAdapter(context)

            binding.rvHomeMostPopularVideo.adapter = mostPopularVideoAdapter

            mostPopularVideoAdapter.submitList(item.list.toList())
            // 영상 클릭 이벤트
            mostPopularVideoAdapter.videoClick =
                object : MostPopularVideoAdapter.MostPopularVideoClick {
                    override fun onClick(videoData: VideoForUi) {
                        detailMostPopularVideo?.move(videoData)
                    }
                }
        }
    }

    // 스피너 영역 뷰홀더
    inner class SpinnerViewHolder(private val binding: LayoutSelectCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.Spinner) = with(binding) {

            val adapter = ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                item.categories
            )

            spinnerHomeSelectCategory.adapter = adapter
            spinnerHomeSelectCategory.setSelection(categorySpinnerIdx)
            // 스피너 아이템 선택 리스너
            spinnerHomeSelectCategory.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if (categorySpinnerIdx != p2) {
                            communicateVideoByCategory?.call(
                                YoutubeCategory.entries[p2].id,
                                sortSpinnerIdx
                            )
                        }

                        categorySpinnerIdx = p2
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
        }
    }

    // 텍스트 영역 뷰홀더
    inner class TitleViewHolder(private val binding: LayoutThemeTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.Title) {

            binding.tvHomeThemeTitle.text = item.title
        }
    }

    // 스피너있는 텍스트 영역 뷰홀더
    inner class TitleWithViewHolder(private val binding: LayoutThemeTitleWithSpinnerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.TitleWithSpinner) = with(binding) {

            tvHomeThemeTitleWithSpinner.text = item.title

            val adapter = ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                item.categories
            )

            spinnerHomeSortVideo.adapter = adapter
            spinnerHomeSortVideo.setSelection(sortSpinnerIdx)
            spinnerHomeSortVideo.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if (sortSpinnerIdx != p2) sortCategoryVideo?.sort(p2)
                        sortSpinnerIdx = p2
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                    }
                }
        }
    }

    companion object {
        private const val CHANNEL = 1
        private const val VIDEO = 2
        private const val POPULARVIDEO = 3
        private const val SPINNER = 4
        private const val TITLE = 5
        private const val TITLE_SPINNER = 6
    }
}