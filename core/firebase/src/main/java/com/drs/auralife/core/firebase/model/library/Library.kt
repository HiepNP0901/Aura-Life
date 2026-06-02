package com.drs.auralife.core.firebase.model.library

data class Library(
    var name: String,
    val posterUrl: String,
    val listFilm: MutableList<FilmLibrary>,
)
