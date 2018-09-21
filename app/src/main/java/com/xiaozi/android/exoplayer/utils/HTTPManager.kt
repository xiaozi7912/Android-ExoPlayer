package com.xiaozi.android.exoplayer.utils

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface HTTPManager {
    @GET("youtube/{videoId}")
    fun getUrl(@Path("videoId") videoId: String): Call<YoutubeModel>
}