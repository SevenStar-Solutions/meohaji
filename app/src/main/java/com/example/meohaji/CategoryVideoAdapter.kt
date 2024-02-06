package com.example.meohaji

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.databinding.LayoutVideoByCategoryBigBinding
import com.example.meohaji.fragment.DummyDetail
import com.example.meohaji.fragment.GoDetail
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

class CategoryVideoAdapter(private val context: Context) :
    ListAdapter<CategoryVideo, CategoryVideoAdapter.CategoryVideoViewHolder>(
        object : DiffUtil.ItemCallback<CategoryVideo>() {
            override fun areItemsTheSame(oldItem: CategoryVideo, newItem: CategoryVideo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CategoryVideo,
                newItem: CategoryVideo
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVideoViewHolder {
        return CategoryVideoViewHolder(
            LayoutVideoByCategoryBigBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryVideoViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }

    inner class CategoryVideoViewHolder(private val binding: LayoutVideoByCategoryBigBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: CategoryVideo) = with(binding) {
            Glide.with(context)
                .load(item.thumbnail)
                .into(ivThumbnail)

            tvVideoTitle.text = item.title
            tvChannelName.text = item.channelTitle
            tvUploadDate.text = outputFormat.format(inputFormat.parse(item.publishedAt) as Date)
            textView4.text = item.recommendScore.toString()

            // 인터페이스에 DummyDetail이라는 data class의 값을 담음
            itemView.setOnClickListener {
                goDetail?.sendData(it, DummyDetail(item.title,item.thumbnail, item.likeCount, item.viewCount, item.commentCount, item.publishedAt, item.description))
            }
        }
    }
    var goDetail: GoDetail? = null
}