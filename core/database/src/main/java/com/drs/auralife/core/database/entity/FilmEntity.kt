package com.drs.auralife.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "film_entity")
data class FilmEntity(
    @PrimaryKey val slug: String,
    val title: String,
    val posterUrl: String,
    val thumbUrl: String,
    val description: String,
    val category: String,
    val episodeCount: Int,
    val modifiedAt: Long = 0,
)
