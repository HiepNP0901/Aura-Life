package com.drs.auralife.data.model.film

import com.google.gson.annotations.SerializedName

data class Tmdb(
    val id: String,
    val season: Int,
    val type: String,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
)
