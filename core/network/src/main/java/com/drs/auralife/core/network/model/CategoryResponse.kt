package com.drs.auralife.data.remote.api.model.category

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("_id") val id: String,
    val name: String,
    val slug: String,
)
