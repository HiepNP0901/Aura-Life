package com.drs.auralife.core.network.model.search

import com.google.gson.annotations.SerializedName

data class BreadCrumb(
    @SerializedName("isCurrent") val isCurrent: Boolean,
    @SerializedName("name") val name: String,
    @SerializedName("position") val position: Int,
    val slug: String,
)
