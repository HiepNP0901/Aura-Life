package com.drs.auralife.data.model.search

import com.google.gson.annotations.SerializedName

data class BreadCrumb(
    @SerializedName("isCurrent") val isCurrent: Boolean,
    @SerializedName("name") val name: String,
    @SerializedName("position") val position: Int
)