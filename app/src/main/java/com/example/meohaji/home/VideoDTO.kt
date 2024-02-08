package com.example.meohaji.home

import com.google.gson.annotations.SerializedName

data class Video (
    @SerializedName("kind")
    val kind: String,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("items")
    val items: List<Item>,
    @SerializedName("nextPageToken")
    val nextPageToken: String,
    @SerializedName("pageInfo")
    val pageInfo: PageInfo
)

data class Item (
    @SerializedName("kind")
    val kind: Kind,
    @SerializedName("etag")
    val etag: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("snippet")
    val snippet: Snippet,
    @SerializedName("statistics")
    val statistics: Statistics
)

enum class Kind(
    val value: String
) {
    @SerializedName("youtube#video")
    YoutubeVideo("youtube#video")
}

data class Snippet (
    @SerializedName("publishedAt")
    val publishedAt: String,
    @SerializedName("channelId")
    val channelID: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("thumbnails")
    val thumbnails: Thumbnails,
    @SerializedName("channelTitle")
    val channelTitle: String,
    @SerializedName("categoryId")
    val categoryID: String,
    @SerializedName("liveBroadcastContent")
    val liveBroadcastContent: LiveBroadcastContent,
    @SerializedName("localized")
    val localized: Localized,
    @SerializedName("tags")
    val tags: List<String>? = null,
    @SerializedName("defaultAudioLanguage")
    val defaultAudioLanguage: DefaultLanguage? = null,
    @SerializedName("defaultLanguage")
    val defaultLanguage: DefaultLanguage? = null
)

enum class DefaultLanguage(
    val value: String
) {
    @SerializedName("en")
    En("en"),
    @SerializedName("en-US")
    EnUS("en-US"),
    @SerializedName("ko")
    Ko("ko")
}

enum class LiveBroadcastContent(
    val value: String
) {
    @SerializedName("none")
    None("none")
}

data class Localized (
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String
)

data class Thumbnails (
    @SerializedName("default")
    val default: Default,
    @SerializedName("medium")
    val medium: Default,
    @SerializedName("high")
    val high: Default,
    @SerializedName("standard")
    val standard: Default,
    @SerializedName("maxres")
    val maxres: Default
)

data class Default (
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Long,
    @SerializedName("height")
    val height: Long
)

data class Statistics (
    @SerializedName("viewCount")
    val viewCount: String,
    @SerializedName("likeCount")
    val likeCount: String,
    @SerializedName("favoriteCount")
    val favoriteCount: String,
    @SerializedName("commentCount")
    val commentCount: String
)

data class PageInfo (
    @SerializedName("totalResults")
    val totalResults: Long,
    @SerializedName("resultsPerPage")
    val resultsPerPage: Long
)

