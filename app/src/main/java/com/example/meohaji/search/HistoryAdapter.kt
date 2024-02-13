package com.example.meohaji.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.ItemLatestSearchWordBinding
import com.example.meohaji.databinding.ItemVideoByCategoryBinding

class HistoryAdapter : ListAdapter<HistoryList, HistoryAdapter.ItemViewHolder>(object :
    DiffUtil.ItemCallback<HistoryList>() {
    override fun areItemsTheSame(oldItem: HistoryList, newItem: HistoryList): Boolean {
        // 비디오 id가 같은지 확인
        return oldItem.query == newItem.query
    }

    override fun areContentsTheSame(oldItem: HistoryList, newItem: HistoryList): Boolean {
        // 모든 필드가 같은지 확인 (data class의 equals 사용)
        return oldItem == newItem
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemLatestSearchWordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))

    }


    inner class ItemViewHolder(val binding: ItemLatestSearchWordBinding) :
        RecyclerView.ViewHolder(binding.root){

        val query = binding.tvLatestSearchWord


        fun bind(historyList: HistoryList) {
            query.text = historyList.query
        }



    }

}