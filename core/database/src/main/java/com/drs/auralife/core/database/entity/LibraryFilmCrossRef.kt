package com.drs.auralife.core.database.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "library_film_cross_ref",
    primaryKeys = ["libraryName", "filmSlug"],
    indices = [Index("filmSlug")],
)
data class LibraryFilmCrossRef(
    val libraryName: String,
    val filmSlug: String,
    val currentEpisode: String,
)
