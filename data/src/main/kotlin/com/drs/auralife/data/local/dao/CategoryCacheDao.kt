package com.drs.auralife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drs.auralife.data.local.entity.CategoryCacheEntity

@Dao
interface CategoryCacheDao {
    @Query("SELECT * FROM category_cache_entity")
    suspend fun getAll(): List<CategoryCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryCacheEntity>)

    @Query("DELETE FROM category_cache_entity")
    suspend fun clear()
}
