package com.drs.auralife.core.network.model

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("_id") val id: String,
    val name: String,
    val slug: String,
)
