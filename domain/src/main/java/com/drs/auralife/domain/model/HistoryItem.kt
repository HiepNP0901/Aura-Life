package com.drs.auralife.domain.model

data class HistoryItem(
    val slug: String,
    val title: String,
    val watchedAt: Long,
    val episode: Int = 0,
    val position: Long = 0,
)
