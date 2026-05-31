package com.drs.auralife.data.remote.api.model.search

import com.drs.auralife.data.remote.api.model.films.Pagination
import com.google.gson.annotations.SerializedName

data class Params(
    val slug: String?,
    val keyword: String?,
    val filterCategory: List<String>,
    val filterCountry: List<String>,
    val filterType: List<String>,
    val filterYear: List<String>,
    val pagination: Pagination,
    val sortField: String,
    val sortType: String,
    @SerializedName("type_slug") val typeSlug: String,
)
