package com.drs.auralife.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banner_cache_entity")
data class BannerCacheEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUrl: String,
    val filmSlug: String,
)
