package com.drs.auralife.core.network.model

import com.google.gson.annotations.SerializedName

data class CategoriesResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Data,
) {
    data class Data(
        @SerializedName("items") val items: List<CategoryResponse>,
    )
}

data class CategoryResponse(
    @SerializedName("_id") val id: String,
    val name: String,
    val slug: String,
)
