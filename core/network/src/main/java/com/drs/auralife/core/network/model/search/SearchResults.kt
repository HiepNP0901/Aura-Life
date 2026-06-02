package com.drs.auralife.core.network.model.search

import com.google.gson.annotations.SerializedName

data class SearchResults(
    @SerializedName("data") val data: Data,
    @SerializedName("msg") val msg: String,
    @SerializedName("status") val status: String,
)
