package com.drs.auralife.data.model.films

import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("_id") val itemID: String,
    val modified: Modified,
    val name: String,
    @SerializedName("origin_name") val originName: String,
    @SerializedName("poster_url") val posterUrl: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("thumb_url") val thumbUrl: String,
    val year: Int
)