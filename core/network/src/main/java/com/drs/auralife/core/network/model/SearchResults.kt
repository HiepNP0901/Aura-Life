package com.drs.auralife.core.network.model

import com.google.gson.annotations.SerializedName

data class SearchResults(
    @SerializedName("data") val data: Data,
    @SerializedName("msg") val msg: String,
    @SerializedName("status") val status: String,
) {
    data class Data(
        @SerializedName("APP_DOMAIN_CDN_IMAGE") val appDomainCdnImage: String,
        @SerializedName("APP_DOMAIN_FRONTEND") val appDomainFrontend: String,
        val breadCrumb: List<BreadCrumb>,
        val items: List<Movie>,
        val params: Params,
        val seoOnPage: SeoOnPage,
        val titlePage: String,
        @SerializedName("type_list") val typeList: String,
    ) {
        data class BreadCrumb(
            @SerializedName("isCurrent") val isCurrent: Boolean,
            @SerializedName("name") val name: String,
            @SerializedName("position") val position: Int,
            val slug: String,
        )
    }

    data class Params(
        val slug: String?,
        val keyword: String?,
        val filterCategory: List<String>,
        val filterCountry: List<String>,
        val filterType: List<String>,
        val filterYear: List<String>,
        val pagination: Films.Pagination,
        val sortField: String,
        val sortType: String,
        @SerializedName("type_slug") val typeSlug: String,
    )

    data class SeoOnPage(
        val descriptionHead: String,
        @SerializedName("og_image") val ogImage: List<String>,
        @SerializedName("og_type") val ogType: String,
        @SerializedName("og_url") val ogUrl: String,
        val titleHead: String,
    )
}
