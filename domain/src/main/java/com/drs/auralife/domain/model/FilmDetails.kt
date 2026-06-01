package com.drs.auralife.domain.model

data class FilmDetails(
    val id: String,
    val slug: String,
    val title: String,
    val originName: String,
    val posterUrl: String,
    val thumbUrl: String,
    val trailerUrl: String?,
    val description: String,
    val episodeCurrent: String?,
    val episodeTotal: String?,
    val quality: String?,
    val language: String?,
    val duration: String?,
    val year: Int?,
    val status: String?,
    val directors: List<String>?,
    val actors: List<String>?,
    val categories: List<String>?,
    val countries: List<String>?,
    val episodes: List<FilmEpisode>,
)
