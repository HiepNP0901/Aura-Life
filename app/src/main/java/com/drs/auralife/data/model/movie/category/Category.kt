package com.drs.auralife.data.model.movie.category

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("1") val category1: DataCategory,
    @SerializedName("2") val category2: DataCategory,
    @SerializedName("3") val category3: DataCategory,
    @SerializedName("4") val category4: DataCategory,
)