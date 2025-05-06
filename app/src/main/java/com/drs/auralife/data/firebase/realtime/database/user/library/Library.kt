package com.drs.auralife.data.firebase.realtime.database.user.library

data class Library(
    var name: String,
    val posterUrl: String,
    val listFilm: MutableList<FilmLibrary>,
)
