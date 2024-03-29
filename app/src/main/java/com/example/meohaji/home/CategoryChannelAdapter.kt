package com.example.meohaji.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.ItemChannelByCategoryBinding

class CategoryChannelAdapter(private val context: Context) :
    ListAdapter<CategoryChannel, CategoryChannelAdapter.CategoryChannelViewHolder>(
        object : DiffUtil.ItemCallback<CategoryChannel>() {
            override fun areItemsTheSame(
                oldItem: CategoryChannel,
                newItem: CategoryChannel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CategoryChannel,
                newItem: CategoryChannel
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    interface VideoChannelClick {
        fun onClick(channelData: CategoryChannel)
    }

    var videoChannelClick: VideoChannelClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryChannelViewHolder {
        return CategoryChannelViewHolder(
            ItemChannelByCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryChannelViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class CategoryChannelViewHolder(private val binding: ItemChannelByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: CategoryChannel) = with(binding) {
            Glide.with(context)
                .load(item.thumbnail)
                .into(civChannelByCategoryItemThumbnail)

            tvChannelByCategoryItemName.text = item.title
            itemView.setOnClickListener {
                videoChannelClick?.onClick(item)
            }
        }
    }
}