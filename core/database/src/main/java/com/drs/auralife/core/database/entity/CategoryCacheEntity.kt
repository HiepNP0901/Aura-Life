package com.drs.auralife.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category_cache_entity")
data class CategoryCacheEntity(
    @PrimaryKey val slug: String,
    val name: String,
    val localizedName: String,
)
