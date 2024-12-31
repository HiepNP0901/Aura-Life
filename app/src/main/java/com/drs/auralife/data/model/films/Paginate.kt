package com.drs.auralife.data.model.films

import com.google.gson.annotations.SerializedName

data class Paginate(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("total_page") val totalPage: Int,
    @SerializedName("total_items") val totalItems: Int,
    @SerializedName("items_per_page") val itemsPerPage: Int
)
