package com.drs.auralife.data.model.film

import com.google.gson.annotations.SerializedName

data class ServerData(
    val filename: String,
    @SerializedName("link_embed") val linkEmbed: String,
    @SerializedName("link_m3u8") val linkM3u8: String,
    val name: String,
    val slug: String
)