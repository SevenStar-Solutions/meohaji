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
import com.example.meohaji.databinding.ItemVideoByCategoryBinding
import com.example.meohaji.databinding.LayoutChannelByCategoryBinding
import com.example.meohaji.databinding.LayoutMostPopularVideoBinding
import com.example.meohaji.databinding.LayoutSelectCategoryBinding
import com.example.meohaji.databinding.LayoutThemeTitleBinding
import com.example.meohaji.databinding.LayoutThemeTitleWithSpinnerBinding
import com.google.android.material.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeAdapter(private val context: Context) :
    ListAdapter<HomeUiData, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<HomeUiData>() {
        override fun areItemsTheSame(oldItem: HomeUiData, newItem: HomeUiData): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: HomeUiData, newItem: HomeUiData): Boolean {
            return oldItem == newItem
        }

    }) {

    interface CommunicateVideoByCategory {
        fun call(id: String, sortOrder: Int)
    }

    interface DetailMostPopularVideo {
        fun move(videoData: MostPopularVideo)
    }

    interface DetailCategoryVideo {
        fun move(videoData: CategoryVideo)
    }

    interface SortCategoryVideo {
        fun sort(order: Int)
    }

    var communicateVideoByCategory: CommunicateVideoByCategory? = null
    var detailMostPopularVideo: DetailMostPopularVideo? = null
    var detailCategoryVideo: DetailCategoryVideo? = null
    var sortCategoryVideo: SortCategoryVideo? = null

    private var categorySpinnerIdx = 0
    private var sortSpinnerIdx = 0

    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

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
        when (currentList[position]) {
            is HomeUiData.Title -> (holder as TitleViewHolder).bind(currentList[position] as HomeUiData.Title)
            is HomeUiData.CategoryChannels -> (holder as CategoryChannelViewHolder).bind(currentList[position] as HomeUiData.CategoryChannels)
            is HomeUiData.CategoryVideos -> (holder as CategoryVideoViewHolder).bind(currentList[position] as HomeUiData.CategoryVideos)
            is HomeUiData.MostPopularVideos -> (holder as MostPopularVideoViewHolder).bind(
                currentList[position] as HomeUiData.MostPopularVideos
            )
            is HomeUiData.Spinner -> (holder as SpinnerViewHolder).bind(currentList[position] as HomeUiData.Spinner)
            is HomeUiData.TitleWithSpinner -> (holder as TitleWithViewHolder).bind(currentList[position] as HomeUiData.TitleWithSpinner)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is HomeUiData.CategoryChannels -> CHANNEL
            is HomeUiData.CategoryVideos -> VIDEO
            is HomeUiData.MostPopularVideos -> POPULARVIDEO
            is HomeUiData.Spinner -> SPINNER
            is HomeUiData.Title -> TITLE
            is HomeUiData.TitleWithSpinner -> TITLE_SPINNER
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

    inner class CategoryChannelViewHolder(private val binding: LayoutChannelByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.CategoryChannels) {
            val categoryChannelAdapter = CategoryChannelAdapter(context)
            binding.rvHomeChannelByCategory.adapter = categoryChannelAdapter
            categoryChannelAdapter.submitList(item.list.toList())
        }
    }

    inner class CategoryVideoViewHolder(private val binding: ItemVideoByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.CategoryVideos) = with(binding) {
            Glide.with(context)
                .load(item.video.thumbnail)
                .into(ivVideoByCategoryItemThumbnail)

            tvVideoByCategoryItemTitle.text = item.video.title
            tvVideoByCategoryItemChannelName.text = item.video.channelTitle
            tvVideoByCategoryItemUploadDate.text =
                outputFormat.format(inputFormat.parse(item.video.publishedAt) as Date)
            tvVideoByCategoryItemRecommendScore.text = item.video.recommendScore.toString()
            binding.ivVideoByCategoryItemThumbnail.setOnClickListener {
                detailCategoryVideo?.move(item.video)
            }
        }
    }

    inner class MostPopularVideoViewHolder(private val binding: LayoutMostPopularVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.MostPopularVideos) {
            val mostPopularVideoAdapter = MostPopularVideoAdapter(context)
            binding.rvHomeMostPopularVideo.adapter = mostPopularVideoAdapter
            mostPopularVideoAdapter.submitList(item.list.toList())
            mostPopularVideoAdapter.videoClick =
                object : MostPopularVideoAdapter.MostPopularVideoClick {
                    override fun onClick(videoData: MostPopularVideo) {
                        detailMostPopularVideo?.move(videoData)
                    }
                }
        }
    }

    inner class SpinnerViewHolder(private val binding: LayoutSelectCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.Spinner) {
            val adapter = ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                item.categories
            )
            binding.spinnerHomeSelectCategory.adapter = adapter

            binding.spinnerHomeSelectCategory.setSelection(categorySpinnerIdx)
            binding.spinnerHomeSelectCategory.onItemSelectedListener =
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
                        TODO("Not yet implemented")
                    }
                }
        }
    }

    inner class TitleViewHolder(private val binding: LayoutThemeTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.Title) {
            binding.tvHomeThemeTitle.text = item.title
        }
    }

    inner class TitleWithViewHolder(private val binding: LayoutThemeTitleWithSpinnerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.TitleWithSpinner) {
            binding.tvHomeThemeTitleWithSpinner.text = item.title

            val adapter = ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                item.categories
            )
            binding.spinnerHomeSortVideo.adapter = adapter

            binding.spinnerHomeSortVideo.setSelection(sortSpinnerIdx)
            binding.spinnerHomeSortVideo.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        if (sortSpinnerIdx != p2) sortCategoryVideo?.sort(p2)
                        sortSpinnerIdx = p2
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
        }
    }
}