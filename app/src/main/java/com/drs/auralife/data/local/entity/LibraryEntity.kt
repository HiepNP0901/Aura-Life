package com.drs.auralife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_entity")
data class LibraryEntity(
    @PrimaryKey val name: String,
    val posterUrl: String,
)
