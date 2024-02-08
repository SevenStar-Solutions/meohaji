package com.example.meohaji.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.ItemVideoByCategoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CategoryVideoAdapter(private val context: Context) :
    ListAdapter<CategoryVideo, CategoryVideoAdapter.CategoryVideoViewHolder>(
        object : DiffUtil.ItemCallback<CategoryVideo>() {
            override fun areItemsTheSame(oldItem: CategoryVideo, newItem: CategoryVideo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CategoryVideo,
                newItem: CategoryVideo
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    interface CategoryVideoClick {
        fun onClick(videoData: CategoryVideo)
    }

    var videoClick: CategoryVideoClick? = null

    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVideoViewHolder {
        return CategoryVideoViewHolder(
            ItemVideoByCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryVideoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class CategoryVideoViewHolder(private val binding: ItemVideoByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: CategoryVideo) = with(binding) {
            Glide.with(context)
                .load(item.thumbnail)
                .into(ivVideoByCategoryItemThumbnail)

            tvVideoByCategoryItemTitle.text = item.title
            tvVideoByCategoryItemChannelName.text = item.channelTitle
            tvVideoByCategoryItemUploadDate.text = outputFormat.format(inputFormat.parse(item.publishedAt) as Date)
            tvVideoByCategoryItemRecommendScore.text = item.recommendScore.toString()

            itemView.setOnClickListener {
                videoClick?.onClick(item)
            }
        }
    }
}