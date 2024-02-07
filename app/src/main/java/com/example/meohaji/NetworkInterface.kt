package com.example.meohaji

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface NetworkInterface {

    @GET("videos")
    fun mostPopularVideos(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("chart") chart: String?,
        @Query("regionCode") code: String?
    ): Call<Video?>?

    @GET("videos")
    fun videoByCategory(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("chart") chart: String?,
        @Query("maxResults") max: Int?,
        @Query("regionCode") code: String?,
        @Query("videoCategoryId") categoryId: String?,
    ): Call<Video?>?

    @GET("channels")
    fun channelByCategory(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("id") channelId: String?
    ) : Call<Channel?>?
}