package com.example.meohaji.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.ItemVideoByCategoryBinding

class SearchAdapter : ListAdapter<SearchList, SearchAdapter.ItemViewHolder>(object :
    DiffUtil.ItemCallback<SearchList>() {
    override fun areItemsTheSame(oldItem: SearchList, newItem: SearchList): Boolean {
        // 비디오 id가 같은지 확인
        return (oldItem.thumbnail == newItem.thumbnail) && (oldItem.title == newItem.title)
    }

    override fun areContentsTheSame(oldItem: SearchList, newItem: SearchList): Boolean {
        // 모든 필드가 같은지 확인 (data class의 equals 사용)
        return oldItem == newItem
    }
}) {

    interface SearchVideoClick {
        fun onClick(videoData: SearchList)
    }

    var videoClick: SearchVideoClick? = null

    inner class ItemViewHolder(val binding: ItemVideoByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.ivVideoByCategoryItemThumbnail
        val title = binding.tvVideoByCategoryItemTitle
        val channel = binding.tvVideoByCategoryItemChannelName
        val time = binding.tvVideoByCategoryItemUploadDate
        val score = binding.tvVideoByCategoryItemRecommendScore
        fun bind(searchList: SearchList) {
            title.text = getItem(adapterPosition).title
            channel.text = getItem(adapterPosition).channelTitle
            time.text = getItem(adapterPosition).publishedAt
            score.visibility = View.INVISIBLE
            Glide.with(image)
                .load(searchList.thumbnail)
                .into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemVideoByCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        //클릭이 호출됐을때
        holder.itemView.setOnClickListener {
            videoClick?.onClick(item)
        }
    }
}