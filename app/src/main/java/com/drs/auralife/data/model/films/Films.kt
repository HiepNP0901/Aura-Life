package com.drs.auralife.data.model.films

import com.google.gson.annotations.SerializedName

data class Films(
    @SerializedName("status") val status: String,
    @SerializedName("paginate") val paginate: Paginate,
    @SerializedName("cat") val category: Category,
    @SerializedName("items") val items: List<FilmPreviews>
)