package com.example.meohaji.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.ItemMostPopularVideoBinding

class MostPopularVideoAdapter(private val context: Context) :
    ListAdapter<VideoForUi, MostPopularVideoAdapter.MostPopularVideoViewHolder>(
        object : DiffUtil.ItemCallback<VideoForUi>() {
            override fun areItemsTheSame(oldItem: VideoForUi, newItem: VideoForUi): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: VideoForUi, newItem: VideoForUi): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    // 영상 상세 페이지로 이동할 때 사용되는 인터페이스
    interface MostPopularVideoClick {
        fun onClick(videoData: VideoForUi)
    }

    var videoClick: MostPopularVideoClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostPopularVideoViewHolder {
        return MostPopularVideoViewHolder(
            ItemMostPopularVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MostPopularVideoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class MostPopularVideoViewHolder(private val binding: ItemMostPopularVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: VideoForUi) = with(binding) {
            Glide.with(context)
                .load(item.thumbnail)
                .into(ivMostPopularVideoItemThumbnail)

            tvMostPopularVideoItemTitle.text = item.title

            // 영상 클릭 리스너
            itemView.setOnClickListener {
                videoClick?.onClick(item)
            }
        }
    }
}