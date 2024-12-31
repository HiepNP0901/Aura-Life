package com.drs.auralife.data.model.movie

import com.google.gson.annotations.SerializedName

data class Episode(
    @SerializedName("server_name") val serverName: String,
    @SerializedName("items") val items: List<Item>,
)