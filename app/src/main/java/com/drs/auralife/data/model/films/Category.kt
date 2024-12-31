package com.drs.auralife.data.model.films

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("name") val name: String,
    @SerializedName("title") val title: String,
    @SerializedName("slug") val slug: String
)
