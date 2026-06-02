package com.drs.auralife.core.network.model.search

import com.google.gson.annotations.SerializedName

data class SeoOnPage(
    val descriptionHead: String,
    @SerializedName("og_image") val ogImage: List<String>,
    @SerializedName("og_type") val ogType: String,
    @SerializedName("og_url") val ogUrl: String,
    val titleHead: String,
)
