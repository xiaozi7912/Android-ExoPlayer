package com.xiaozi.android.exoplayer.utils

import com.google.gson.annotations.SerializedName

class YoutubeModel {
    @SerializedName("title")
    var title: String? = null

    @SerializedName("url")
    var url: String? = null
}