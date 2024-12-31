package com.drs.auralife.data.model

import com.drs.auralife.data.model.movie.Movie
import com.google.gson.annotations.SerializedName


data class FilmDetails(
    @SerializedName("status") val status: String,
    @SerializedName("movie") val movie: Movie,
)