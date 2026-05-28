package com.drs.auralife.domain.model

data class FilmDetails(
    val id: String,
    val slug: String,
    val title: String,
    val posterUrl: String,
    val thumbUrl: String,
    val description: String,
    val videos: List<String>,
    val metadata: Map<String, String>,
)
