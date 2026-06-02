package com.drs.auralife.core.network.model.search

import com.drs.auralife.core.network.model.film.Movie
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("APP_DOMAIN_CDN_IMAGE") val appDomainCdnImage: String,
    @SerializedName("APP_DOMAIN_FRONTEND") val appDomainFrontend: String,
    val breadCrumb: List<BreadCrumb>,
    val items: List<Movie>,
    val params: Params,
    val seoOnPage: SeoOnPage,
    val titlePage: String,
    @SerializedName("type_list") val typeList: String,
)
