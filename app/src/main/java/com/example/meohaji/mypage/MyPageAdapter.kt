package com.example.meohaji.mypage

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meohaji.R
import com.example.meohaji.Utils
import com.example.meohaji.databinding.ItemVideoByCategoryBinding
import com.example.meohaji.databinding.LayoutMyProfileBinding
import com.example.meohaji.databinding.LayoutSaveVideoTitleBinding
import com.example.meohaji.home.VideoForUi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MyPageAdapter(private val context: Context) :
    ListAdapter<MyPageUiData, RecyclerView.ViewHolder>(
        object : DiffUtil.ItemCallback<MyPageUiData>() {
            override fun areItemsTheSame(oldItem: MyPageUiData, newItem: MyPageUiData): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: MyPageUiData, newItem: MyPageUiData): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    interface EditMyProfile {
        fun open(name: String, image: Drawable?)
    }

    interface DetailSaveVideo {
        fun move(videoData: VideoForUi)
    }

    var editMyProfile: EditMyProfile? = null
    var detailSaveVideo: DetailSaveVideo? = null

    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.getDefault())
    val outputFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is MyPageUiData.Profile -> PROFILE
            is MyPageUiData.Title -> TITLE
            is MyPageUiData.Video -> VIDEO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PROFILE -> {
                ProfileViewHolder(
                    LayoutMyProfileBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            TITLE -> {
                TitleViewHolder(
                    LayoutSaveVideoTitleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                SaveVideoViewHolder(
                    ItemVideoByCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (currentList[position]) {
            is MyPageUiData.Profile -> (holder as ProfileViewHolder).bind(currentList[position] as MyPageUiData.Profile)
            is MyPageUiData.Title -> (holder as TitleViewHolder).bind(currentList[position] as MyPageUiData.Title)
            is MyPageUiData.Video -> (holder as SaveVideoViewHolder).bind(currentList[position] as MyPageUiData.Video)
        }
    }

    inner class ProfileViewHolder(private val binding: LayoutMyProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyPageUiData.Profile) = with(binding) {
            tvMyPageName.text = item.name
            Glide.with(context)
                .load(item.image)
                .into(civMyPageProfile)

            btnMyPageEditName.setOnClickListener {
                editMyProfile?.open(
                    tvMyPageName.text.toString(),
                    if (civMyPageProfile.drawable == null) null else civMyPageProfile.drawable
                )
            }
        }
    }

    inner class TitleViewHolder(private val binding: LayoutSaveVideoTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyPageUiData.Title) = with(binding) {
            val spannableString = SpannableString(tvMyPageSavedVideo.text)
            spannableString.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        context,
                        R.color.yellow_background
                    )
                ),
                0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvMyPageSavedVideo.text = spannableString

            btnClearSavedVideo.setOnClickListener {
                Utils.deletePrefItem(context)
                submitList(currentList.subList(0, 2))
            }
        }
    }

    inner class SaveVideoViewHolder(private val binding: ItemVideoByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyPageUiData.Video) = with(binding) {
            Glide.with(context)
                .load(item.video.thumbnail)
                .into(ivVideoByCategoryItemThumbnail)

            tvVideoByCategoryItemTitle.text = item.video.title
            tvVideoByCategoryItemChannelName.text = item.video.channelTitle
            tvVideoByCategoryItemUploadDate.text =
                outputFormat.format(inputFormat.parse(item.video.publishedAt) as Date)
            tvVideoByCategoryItemRecommendScore.text = item.video.recommendScore.toString()
            ivVideoByCategoryItemThumbnail.setOnClickListener {
                detailSaveVideo?.move(item.video)
            }
        }
    }

    companion object {
        private const val PROFILE = 1
        private const val TITLE = 2
        private const val VIDEO = 3
    }
}

