package com.drs.auralife.data.model.search

import com.drs.auralife.data.model.films.Item
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("APP_DOMAIN_CDN_IMAGE") val appDomainCdnImage: String,
    @SerializedName("APP_DOMAIN_FRONTEND") val appDomainFrontend: String,
    @SerializedName("breadCrumb") val breadCrumb: List<BreadCrumb>,
    @SerializedName("items") val items: List<Item>,
    @SerializedName("params") val params: Params,
    @SerializedName("seoOnPage") val seoOnPage: SeoOnPage,
    @SerializedName("titlePage") val titlePage: String,
    @SerializedName("type_list") val typeList: String
)