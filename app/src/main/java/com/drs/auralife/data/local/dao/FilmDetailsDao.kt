package com.drs.auralife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drs.auralife.data.local.entity.FilmDetailsEntity

@Dao
interface FilmDetailsDao {
    @Query("SELECT * FROM film_details_entity WHERE slug = :slug")
    suspend fun getFilmDetails(slug: String): FilmDetailsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilmDetails(details: FilmDetailsEntity)

    @Query("DELETE FROM film_details_entity")
    suspend fun clearFilmDetails()
}
