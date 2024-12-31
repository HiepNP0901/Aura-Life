package com.drs.auralife.data.model.movie.category

import com.google.gson.annotations.SerializedName

data class DataCategory(
    @SerializedName("group") val group: Group,
    @SerializedName("list") val list: List<Item0>
)