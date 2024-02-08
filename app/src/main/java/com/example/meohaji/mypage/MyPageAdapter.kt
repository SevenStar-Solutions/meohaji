package com.example.meohaji.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.ItemVideoByCategoryBinding


class MyPageAdapter(private val context: Context, private val items: ArrayList<SavedItem>) : RecyclerView.Adapter<MyPageAdapter.Holder>() {

    inner class Holder(private val binding: ItemVideoByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var thumbnail = binding.ivVideoByCategoryItemThumbnail //iv 사용 시 Glide 사용 잊지 말것
        var title = binding.tvVideoByCategoryItemTitle
        var likeCount = binding.tvVideoByCategoryItemChannelName
        var viewCount = binding.tvVideoByCategoryItemUploadDate
        var recommendScore = binding.tvVideoByCategoryItemRecommendScore


    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemVideoByCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = items[position]

        holder.apply {

            Glide.with(context)
                .load(data.thumbnail)
                .into(thumbnail)

            title.text = data.title
            likeCount.text = data.likeCount.toString()
            viewCount.text = data.viewCount.toString()
            recommendScore.text = data.recommendScore


        }
        //클릭 이벤트
    }

}

