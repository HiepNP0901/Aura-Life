package com.drs.auralife.data.model.films

import com.google.gson.annotations.SerializedName

data class FilmPreviews(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("original_name") val originalName: String,
    @SerializedName("thumb_url") val thumbUrl: String,
    @SerializedName("poster_url") val posterUrl: String,
    @SerializedName("created") val created: String,
    @SerializedName("modified") val modified: String,
    @SerializedName("description") val description: String,
    @SerializedName("total_episodes") val totalEpisodes: Int,
    @SerializedName("current_episode") val currentEpisode: String,
    @SerializedName("time") val time: String,
    @SerializedName("quality") val quality: String,
    @SerializedName("language") val language: String,
    @SerializedName("director") val director: String,
    @SerializedName("casts") val casts: String,
)