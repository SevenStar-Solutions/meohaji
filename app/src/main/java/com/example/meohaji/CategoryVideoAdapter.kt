package com.example.meohaji

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.ConstraintVideoImageBigBinding
import com.example.meohaji.databinding.LayoutChannelByCategoryBinding

class CategoryVideoAdapter(private val context: Context): ListAdapter<CategoryVideo, CategoryVideoAdapter.CategoryVideoViewHolder>(
    object : DiffUtil.ItemCallback<CategoryVideo>() {
        override fun areItemsTheSame(oldItem: CategoryVideo, newItem: CategoryVideo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryVideo, newItem: CategoryVideo): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVideoViewHolder {
        return CategoryVideoViewHolder(
            ConstraintVideoImageBigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryVideoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class CategoryVideoViewHolder(private val binding: ConstraintVideoImageBigBinding): RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: CategoryVideo) = with(binding) {
            Glide.with(context)
                .load(item.thumbnail)
                .into(ivThumbnail)

            tvVideoTitle.text = item.title
            tvChannelName.text = item.channelTitle
            tvUploadDate.text = item.publishedAt
        }
    }
}