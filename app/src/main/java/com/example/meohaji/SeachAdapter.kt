package com.example.meohaji

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.LayoutVideoByCategoryBigBinding

class SeachAdapter(private val mContext: Context) :

    ListAdapter<SeachList, SeachAdapter.ItemViewHolder>(object :
        DiffUtil.ItemCallback<SeachList>() {
        override fun areItemsTheSame(oldItem: SeachList, newItem: SeachList): Boolean {
            // 비디오 id가 같은지 확인
            return (oldItem.thumbnail == newItem.thumbnail) && (oldItem.title == newItem.title)
        }

        override fun areContentsTheSame(oldItem: SeachList, newItem: SeachList): Boolean {
            // 모든 필드가 같은지 확인 (data class의 equals 사용)
            return oldItem == newItem
        }
    }) {

    inner class ItemViewHolder(val binding: LayoutVideoByCategoryBigBinding) :
        RecyclerView.ViewHolder(binding.root){
        val image = binding.ivThumbnail
        val title = binding.tvVideoTitle
        val channel = binding.tvChannelName
        val time = binding.tvUploadDate
        val score = binding.textView4
            fun bind(seachList: SeachList){
                title.text = getItem(adapterPosition).title
                channel.text = getItem(adapterPosition).channel
                time.text = getItem(adapterPosition).time
                score.text = getItem(adapterPosition).score
                Glide.with(image)
                    .load(seachList.thumbnail)
                    .into(image)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeachAdapter.ItemViewHolder {
        return ItemViewHolder(
            LayoutVideoByCategoryBigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SeachAdapter.ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

}