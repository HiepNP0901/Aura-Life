package com.drs.auralife.data.model.search

import com.drs.auralife.data.model.films.Pagination
import com.google.gson.annotations.SerializedName

data class Params(
    @SerializedName("filterCategory") val filterCategory: List<String>,
    @SerializedName("filterCountry") val filterCountry: List<String>,
    @SerializedName("filterType") val filterType: String,
    @SerializedName("filterYear") val filterYear: String,
    @SerializedName("keyword") val keyword: String,
    @SerializedName("pagination") val pagination: Pagination,
    @SerializedName("sortField") val sortField: String,
    @SerializedName("sortType") val sortType: String,
    @SerializedName("type_slug") val typeSlug: String
)