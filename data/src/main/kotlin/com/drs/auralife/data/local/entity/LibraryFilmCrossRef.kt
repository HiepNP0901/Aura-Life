package com.drs.auralife.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "library_film_cross_ref",
    primaryKeys = ["libraryName", "filmSlug"],
    indices = [androidx.room.Index("filmSlug")],
)
data class LibraryFilmCrossRef(
    val libraryName: String,
    val filmSlug: String,
    val currentEpisode: String,
)
