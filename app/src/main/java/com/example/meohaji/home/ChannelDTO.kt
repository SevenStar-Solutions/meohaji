package com.example.meohaji.home

import com.google.gson.annotations.SerializedName

data class Channel (
    @SerializedName("kind")
    val kind: String,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("pageInfo")
    val pageInfo: ChannelPageInfo,
    @SerializedName("items")
    val items: List<ChannelItem>
)

data class ChannelItem (
    @SerializedName("kind")
    val kind: String,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("snippet")
    val snippet: ChannelSnippet,
    @SerializedName("statistics")
    val statistics: ChannelStatistics
)

data class ChannelSnippet (
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("customUrl")
    val customURL: String?,
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("thumbnails")
    val thumbnails: ChannelThumbnails,
    @SerializedName("defaultLanguage")
    val defaultLanguage: String? = null,
    @SerializedName("localized")
    val localized: ChannelLocalized,
    @SerializedName("country")
    val country: String
)

data class ChannelLocalized (
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String
)

data class ChannelThumbnails (
    @SerializedName("default")
    val default: ChannelDefault,
    @SerializedName("medium")
    val medium: ChannelDefault,
    @SerializedName("high")
    val high: ChannelDefault
)

data class ChannelDefault (
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Long,
    @SerializedName("height")
    val height: Long
)

data class ChannelStatistics (
    @SerializedName("viewCount")
    val viewCount: String?,
    @SerializedName("subscriberCount")
    val subscriberCount: String?,
    @SerializedName("hiddenSubscriberCount")
    val hiddenSubscriberCount: Boolean,
    @SerializedName("videoCount")
    val videoCount: String?
)

data class ChannelPageInfo (
    @SerializedName("totalResults")
    val totalResults: Long,
    @SerializedName("resultsPerPage")
    val resultsPerPage: Long
)