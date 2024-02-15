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
import com.example.meohaji.databinding.ItemNoItemBinding
import com.example.meohaji.databinding.ItemVideoByCategoryBinding
import com.example.meohaji.databinding.LayoutMyProfileBinding
import com.example.meohaji.databinding.LayoutSaveVideoTitleBinding
import com.example.meohaji.home.VideoForUi
import java.util.Date

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

    // 프로필 편집 페이지로 이동할 때 사용되는 인터페이스
    interface EditMyProfile {
        fun open(name: String, image: Drawable?)
    }

    // 영상 상세 페이지로 이동할 때 사용되는 인터페이스
    interface DetailSaveVideo {
        fun move(videoData: VideoForUi)
    }

    var editMyProfile: EditMyProfile? = null
    var detailSaveVideo: DetailSaveVideo? = null

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is MyPageUiData.Profile -> PROFILE
            is MyPageUiData.Title -> TITLE
            is MyPageUiData.Video -> VIDEO
            else -> TEXT
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

            VIDEO -> {
                SaveVideoViewHolder(
                    ItemVideoByCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            else -> {
                TextViewHolder(
                    ItemNoItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItem(position)) {
            is MyPageUiData.Profile -> (holder as ProfileViewHolder).bind(currentList[position] as MyPageUiData.Profile)
            is MyPageUiData.Title -> (holder as TitleViewHolder).bind(currentList[position] as MyPageUiData.Title)
            is MyPageUiData.Video -> (holder as SaveVideoViewHolder).bind(currentList[position] as MyPageUiData.Video)
            else -> (holder as TextViewHolder).bind(currentList[position] as MyPageUiData.Text)
        }
    }

    // 프로필 영역 뷰홀더
    inner class ProfileViewHolder(private val binding: LayoutMyProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyPageUiData.Profile) = with(binding) {
            tvMyPageName.text = item.name

            if (item.image == null) {
                civMyPageProfile.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_default_profile
                    )
                )

            } else {
                Glide.with(context)
                    .load(item.image)
                    .into(civMyPageProfile)
            }

            btnMyPageEditName.setOnClickListener {
                editMyProfile?.open(
                    tvMyPageName.text.toString(),
                    if (civMyPageProfile.drawable == null) null else civMyPageProfile.drawable
                )
            }
        }
    }

    // 텍스트 영역 뷰홀더
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
                submitList(currentList.subList(0, 2) + MyPageUiData.Text)
            }
        }
    }

    // 저장된 영상 영역 뷰홀더
    inner class SaveVideoViewHolder(private val binding: ItemVideoByCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyPageUiData.Video) = with(binding) {
            Glide.with(context)
                .load(item.video.thumbnail)
                .into(ivVideoByCategoryItemThumbnail)

            tvVideoByCategoryItemTitle.text = item.video.title
            tvVideoByCategoryItemChannelName.text = item.video.channelTitle
            tvVideoByCategoryItemUploadDate.text =
                Utils.outputFormat.format(Utils.inputFormat.parse(item.video.publishedAt) as Date)
            tvVideoByCategoryItemRecommendScore.text = item.video.recommendScore.toString()
            ivVideoByCategoryItemThumbnail.setOnClickListener {
                detailSaveVideo?.move(item.video)
            }
        }
    }

    // 안내 텍스트 영역 뷰홀더
    inner class TextViewHolder(private val binding: ItemNoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyPageUiData.Text) = Unit
    }

    companion object {
        private const val PROFILE = 1
        private const val TITLE = 2
        private const val VIDEO = 3
        private const val TEXT = 4
    }
}

