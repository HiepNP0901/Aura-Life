package com.drs.auralife.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drs.auralife.core.database.entity.BannerCacheEntity

@Dao
interface BannerCacheDao {
    @Query("SELECT * FROM banner_cache_entity")
    suspend fun getAll(): List<BannerCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(banners: List<BannerCacheEntity>)

    @Query("DELETE FROM banner_cache_entity")
    suspend fun clear()
}
