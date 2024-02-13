package com.example.meohaji

import com.example.meohaji.home.Channel
import com.example.meohaji.home.Video
import com.example.meohaji.search.SearchResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NetworkInterface {

    @GET("videos")
    suspend fun mostPopularVideos(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("chart") chart: String?,
        @Query("regionCode") code: String?
    ): Response<Video>

    @GET("videos")
    suspend fun videoByCategory(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("chart") chart: String?,
        @Query("maxResults") max: Int?,
        @Query("regionCode") code: String?,
        @Query("videoCategoryId") categoryId: String?,
    ): Response<Video>

    @GET("channels")
    suspend fun channelByCategory(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("id") channelId: String?
    ) : Response<Channel>

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
        @Query("pageToken") pageToken: String?
    ) : SearchResult

    @GET("videos")
    suspend fun searchByIdList(
        @Query("key") key: String?,
        @Query("part") part: String?,
        @Query("id") channelId: String?,
    ) : Video
}