package com.drs.auralife.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class LibraryWithFilms(
    @Embedded val library: LibraryEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "filmSlug",
        associateBy = androidx.room.Junction(
            LibraryFilmCrossRef::class,
            parentColumn = "libraryName",
            entityColumn = "filmSlug",
        ),
    )
    val films: List<FilmEntity>,
)
