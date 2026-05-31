package com.drs.auralife.data.local.entity

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
    val createdAt: Long = 0,
)
