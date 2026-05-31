package com.drs.auralife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drs.auralife.data.local.entity.HistoryEntity

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_entity ORDER BY watchedAt DESC")
    suspend fun getAll(): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Query("DELETE FROM history_entity WHERE slug = :slug")
    suspend fun deleteHistory(slug: String)

    @Query("DELETE FROM history_entity")
    suspend fun clear()
}
