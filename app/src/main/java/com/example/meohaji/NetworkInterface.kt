package com.example.meohaji

import retrofit2.http.GET
import retrofit2.http.Query


interface NetworkInterface {

    @GET("videos")
    suspend fun mostPopularVideos(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("chart") chart: String?,
        @Query("regionCode") code: String?
    ): Video

    @GET("videos")
    suspend fun videoByCategory(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("chart") chart: String?,
        @Query("maxResults") max: Int?,
        @Query("regionCode") code: String?,
        @Query("videoCategoryId") categoryId: String?,
    ): Video

    @GET("channels")
    suspend fun channelByCategory(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("id") channelId: String?
    ) : Channel
}