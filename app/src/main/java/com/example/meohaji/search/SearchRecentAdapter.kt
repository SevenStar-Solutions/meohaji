package com.example.meohaji.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.meohaji.R
import com.example.meohaji.databinding.ItemLatestSearchWordBinding

class SearchRecentAdapter:ListAdapter<RecentDataClass, SearchRecentAdapter.Holder>(object :
    DiffUtil.ItemCallback<RecentDataClass>() {
    override fun areItemsTheSame(oldItem: RecentDataClass, newItem: RecentDataClass): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: RecentDataClass, newItem: RecentDataClass): Boolean {
        return oldItem == newItem
    }
}) {

    // 삭제 버튼용 인터페이스 초기화
    interface RecentData {
        fun onDeleted(text: String, name: String)
    }
    var recentData: RecentData? = null

    interface RecentClick {
        fun onClick(name: String)
    }
    var recentClick: RecentClick? = null

    inner class Holder(val binding: ItemLatestSearchWordBinding):RecyclerView.ViewHolder(binding.root) {
        val recent = binding.tvLatestSearchWord
        val delete = binding.ivLatestSearchWordClear
        val layout = binding.layoutLatestSearchWorldConstraint
    }

    override fun getItemCount():Int = currentList.size

    override fun onCurrentListChanged(
        previousList: MutableList<RecentDataClass>,
        currentList: MutableList<RecentDataClass>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        Log.i("This is SearchRecentAdapter","onCurrentListChanged\nprevious : $previousList\ncurrent : $currentList")
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemLatestSearchWordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        Log.i("This is SearchRecentAdapter", "pos : $position, name : ${getItem(position).text}")
        if(currentList.isNotEmpty()) {
            holder.recent.text = getItem(position).text
            holder.delete.setImageResource(R.drawable.ic_close)

            // 최근기록 삭제 버튼을 클릭했을시
            holder.delete.setOnClickListener {
                recentData?.onDeleted(getItem(position).time, getItem(position).text)
            }
            holder.layout.setOnClickListener {
                recentClick?.onClick(getItem(position).text)
            }
        } else {
            holder.recent.text = ""
        }
    }
}