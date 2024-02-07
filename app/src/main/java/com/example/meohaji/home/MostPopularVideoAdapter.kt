package com.example.meohaji.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.LayoutMostPopularVideoBinding

class MostPopularVideoAdapter(private val context: Context): ListAdapter<MostPopularVideo, MostPopularVideoAdapter.MostPopularVideoViewHolder>(
    object : DiffUtil.ItemCallback<MostPopularVideo>() {
        override fun areItemsTheSame(oldItem: MostPopularVideo, newItem: MostPopularVideo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MostPopularVideo, newItem: MostPopularVideo): Boolean {
            return oldItem == newItem
        }
    }
) {
    interface MostPopularVideoClick {
        fun onClick(videoData: MostPopularVideo)
    }

    var videoClick: MostPopularVideoClick? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostPopularVideoViewHolder {
        return MostPopularVideoViewHolder(
            LayoutMostPopularVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MostPopularVideoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class MostPopularVideoViewHolder(private val binding: LayoutMostPopularVideoBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: MostPopularVideo) = with(binding) {
            Glide.with(context)
                .load(item.thumbnail)
                .into(ivMostPopularVideoThumbnail)

            tvMostPopularVideoTitle.text = item.title

            itemView.setOnClickListener {
                videoClick?.onClick(item)
            }
        }
    }
}