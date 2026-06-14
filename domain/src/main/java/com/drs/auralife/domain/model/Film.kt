package com.drs.auralife.domain.model

data class Film(
    val id: String,
    val slug: String,
    val title: String,
    val posterUrl: String,
    val thumbUrl: String,
    val description: String,
    val category: String,
    val episodeCount: Int,
    val modifiedAt: Long = 0,
)
