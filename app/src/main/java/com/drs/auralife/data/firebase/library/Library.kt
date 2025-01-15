package com.drs.auralife.data.firebase.library

import com.google.gson.annotations.SerializedName

data class Library(
    @SerializedName("name") val name: String,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("listSlug") val listSlug: List<String>
)