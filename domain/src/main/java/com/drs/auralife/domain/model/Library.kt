package com.drs.auralife.domain.model

data class Library(
    val name: String,
    val posterUrl: String,
    val films: List<LibraryFilm>,
)

data class LibraryFilm(
    val slug: String,
    val currentEpisode: String,
)
