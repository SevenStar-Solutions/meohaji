package com.example.meohaji

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    // 통신 과정을 로그에서 보기 위해 세팅하는 함수
    private fun createOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(interceptor)
            .build()
    }

    // Retrofit 객체를 Builder를 통해 생성
    private val searchRetrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(createOkHttpClient())
        .build()

    // Retrofit 객체의 create를 통해 NetworkInterface 객체를 생성
    val apiService: NetworkInterface = searchRetrofit.create(NetworkInterface::class.java)
}