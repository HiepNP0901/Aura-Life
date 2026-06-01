package com.drs.auralife.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class LibraryWithFilms(
    @Embedded var library: LibraryEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "slug",
        associateBy = androidx.room.Junction(
            LibraryFilmCrossRef::class,
            parentColumn = "libraryName",
            entityColumn = "filmSlug",
        ),
    )
    var films: List<FilmEntity>,
)
