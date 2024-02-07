package com.example.meohaji

import retrofit2.Call
import com.example.meohaji.Search.SearchResult
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

    // q 타입 max 파트 오더 Regioncode
    @GET("search")
    suspend fun searchByQueryList(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("maxResults") max: Int?,
        @Query("order") order: String?,
        @Query("q") query: String?,
        @Query("regionCode") code: String?,
        @Query("type") type: String?,
    ) : SearchResult
}