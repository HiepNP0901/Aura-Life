package com.drs.auralife.core.firebase.model.library

data class Library(
    val name: String,
    val posterUrl: String,
    val listFilm: List<FilmLibrary>,
)
