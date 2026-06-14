package com.drs.auralife.core.network.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("_id") val movieID: String?,
    val modified: Modified?,
    val name: String?,
    @SerializedName("origin_name") val originName: String?,
    @SerializedName("poster_url") val posterUrl: String?,
    @SerializedName("thumb_url") val thumbUrl: String?,
    @SerializedName("trailer_url") val trailerUrl: String?,
    val tmdb: Tmdb?,
    val view: Int?,
    val year: Int?,
    val imdb: Imdb?,
    val type: String?,
    val lang: String?,
    val time: String?,
    val notify: String?,
    val status: String?,
    val quality: String?,
    val content: String?,
    val created: Created?,
    val actor: List<String>?,
    val country: List<Country>?,
    val director: List<String>?,
    val category: List<Category>?,
    @SerializedName("slug") val slug: String,
    @SerializedName("showtimes") val showTimes: String?,
    @SerializedName("chieu_rap") val chieuRap: Boolean?,
    @SerializedName("sub_docquyen") val subDocquyen: Boolean?,
    @SerializedName("is_copyright") val isCopyright: Boolean?,
    @SerializedName(value = "episode_total") val episodeTotal: String?,
    @SerializedName("episode_current") val episodeCurrent: String?,
) {
    data class Modified(val time: String?)
    data class Created(val time: String?)
    data class Category(val id: String, val name: String, val slug: String)
    data class Country(val id: String, val name: String, val slug: String)
    data class Tmdb(
        val id: String,
        val season: Int,
        val type: String,
        @SerializedName("vote_average") val voteAverage: Double,
        @SerializedName("vote_count") val voteCount: Int,
    )
    data class Imdb(val id: String?)
}
