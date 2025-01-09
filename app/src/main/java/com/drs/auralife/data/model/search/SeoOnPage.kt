package com.drs.auralife.data.model.search

import com.google.gson.annotations.SerializedName

data class SeoOnPage(
    @SerializedName("descriptionHead") val descriptionHead: String,
    @SerializedName("og_image") val ogImage: List<String>,
    @SerializedName("og_type") val ogType: String,
    @SerializedName("og_url") val ogUrl: String,
    @SerializedName("titleHead") val titleHead: String
)