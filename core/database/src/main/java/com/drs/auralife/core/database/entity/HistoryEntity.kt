package com.drs.auralife.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_entity")
data class HistoryEntity(
    @PrimaryKey val slug: String,
    val title: String,
    val watchedAt: Long,
    val episode: Int,
    val position: Long,
)
