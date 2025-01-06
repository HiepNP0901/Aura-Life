package com.drs.auralife.data.model.film

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("_id") val movieID: String,
    val actor: List<String>,
    val category: List<Category>,
    @SerializedName("chieu_rap") val chieuRap: Boolean,
    val content: String,
    val country: List<Country>,
    val created: Created,
    val director: List<String>,
    @SerializedName("episode_current") val episodeCurrent: String,
    @SerializedName("episode_total") val episodeTotal: String,
    val imdb: Imdb,
    @SerializedName("is_copyright") val isCopyright: Boolean,
    val lang: String,
    val modified: Modified,
    val name: String,
    val notify: String,
    @SerializedName("origin_name") val originName: String,
    @SerializedName("poster_url") val posterUrl: String,
    val quality: String,
    @SerializedName("showtimes") val showTimes: String,
    @SerializedName("slug") val slug: String,
    val status: String,
    @SerializedName("sub_docquyen") val subDocquyen: Boolean,
    @SerializedName("thumb_url")val thumbUrl: String,
    val time: String,
    val tmdb: Tmdb,
    @SerializedName("trailer_url") val trailerUrl: String,
    val type: String,
    val view: Int,
    val year: Int
)