package com.drs.auralife.core.network.model.film

data class FilmDetails(
    val episodes: List<Episode>,
    val movie: Movie,
    val msg: String,
    val status: Boolean,
)
