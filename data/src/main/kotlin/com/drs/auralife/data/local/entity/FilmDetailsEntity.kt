package com.drs.auralife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "film_details_entity")
data class FilmDetailsEntity(
    @PrimaryKey val slug: String,
    val title: String,
    val originName: String,
    val posterUrl: String,
    val thumbUrl: String,
    val trailerUrl: String?,
    val description: String,
    val episodeCurrent: String?,
    val episodeTotal: String?,
    val quality: String?,
    val language: String?,
    val duration: String?,
    val year: Int?,
    val status: String?,
    val directors: String?, // JSON
    val actors: String?, // JSON
    val categories: String?, // JSON
    val countries: String?, // JSON
)
