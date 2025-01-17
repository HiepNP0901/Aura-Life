package com.drs.auralife.data.firebase.library

data class Library(
    var name: String,
    val posterUrl: String,
    val listFilm: MutableList<FilmLibrary>
)