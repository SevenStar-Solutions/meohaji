package com.example.meohaji.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.ItemVideoByCategoryBinding
import com.example.meohaji.home.VideoForUi


class MyPageAdapter(private val context: Context) : ListAdapter<VideoForUi, MyPageAdapter.Holder>(
    object : DiffUtil.ItemCallback<VideoForUi>() {
        override fun areItemsTheSame(oldItem: VideoForUi, newItem: VideoForUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoForUi, newItem: VideoForUi): Boolean {
            return oldItem == newItem
        }
    }
) {

    inner class Holder(private val binding: ItemVideoByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoForUi) = with(binding) {
            Glide.with(context)
                .load(item.thumbnail)
                .into(ivVideoByCategoryItemThumbnail)

            tvVideoByCategoryItemTitle.text = item.title
            tvVideoByCategoryItemChannelName.text = item.likeCount.toString()
            tvVideoByCategoryItemUploadDate.text = item.viewCount.toString()
            tvVideoByCategoryItemRecommendScore.text = item.recommendScore.toString()
        }

    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemVideoByCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(currentList[position])
    }

}

