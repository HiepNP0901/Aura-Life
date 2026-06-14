package com.drs.auralife.feature.history.model

data class HistoryFilm(
    val slug: String,
    val title: String,
    val posterUrl: String,
    val description: String,
    val watchedAt: Long,
)
