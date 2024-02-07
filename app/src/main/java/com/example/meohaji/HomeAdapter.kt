package com.example.meohaji

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.SetChannelByCategoryBinding
import com.example.meohaji.databinding.SetMostPopularVideoBinding
import com.example.meohaji.databinding.SetSelectCategoryBinding
import com.example.meohaji.databinding.SetThemeTitleBinding
import com.example.meohaji.databinding.SetThemeTitleWithSpinnerBinding
import com.example.meohaji.databinding.SetVideoByCategoryBinding
import com.example.meohaji.fragment.DetailTags
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
                    SetChannelByCategoryBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            VIDEO -> {
                CategoryVideoViewHolder(
                    SetVideoByCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            POPULARVIDEO -> {
                MostPopularVideoViewHolder(
                    SetMostPopularVideoBinding.inflate(
                        LayoutInflater.from(
                            parent.context
                        ), parent, false
                    )
                )
            }

            SPINNER -> {
                SpinnerViewHolder(
                    SetSelectCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            TITLE -> {
                TitleViewHolder(
                    SetThemeTitleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                TitleWithViewHolder(
                    SetThemeTitleWithSpinnerBinding.inflate(
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

    inner class CategoryChannelViewHolder(private val binding: SetChannelByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.CategoryChannels) {
            val categoryChannelAdapter = CategoryChannelAdapter(context)
            binding.rvHomeCategoryChannel.adapter = categoryChannelAdapter
            categoryChannelAdapter.submitList(item.list.toList())
        }
    }

    inner class CategoryVideoViewHolder(private val binding: SetVideoByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.CategoryVideos) = with(binding) {
            Glide.with(context)
                .load(item.video.thumbnail)
                .into(ivThumbnail)

            tvVideoTitle.text = item.video.title
            tvChannelName.text = item.video.channelTitle
            tvUploadDate.text =
                outputFormat.format(inputFormat.parse(item.video.publishedAt) as Date)
            textView4.text = item.video.recommendScore.toString()
            binding.ivThumbnail.setOnClickListener {
                detailCategoryVideo?.move(item.video)
            }
        }
    }

    inner class MostPopularVideoViewHolder(private val binding: SetMostPopularVideoBinding) :
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

    inner class SpinnerViewHolder(private val binding: SetSelectCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.Spinner) {
            val adapter = ArrayAdapter(
                context,
                R.layout.support_simple_spinner_dropdown_item,
                item.categories
            )
            binding.spinnerHomeCategory.adapter = adapter

            binding.spinnerHomeCategory.setSelection(categorySpinnerIdx)
            binding.spinnerHomeCategory.onItemSelectedListener =
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

    inner class TitleViewHolder(private val binding: SetThemeTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.Title) {
            binding.tvHomeTitle.text = item.title
        }
    }

    inner class TitleWithViewHolder(private val binding: SetThemeTitleWithSpinnerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeUiData.TitleWithSpinner) {
            binding.tvHomeTitle2.text = item.title

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