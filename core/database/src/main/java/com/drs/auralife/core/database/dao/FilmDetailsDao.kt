package com.drs.auralife.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drs.auralife.core.database.entity.FilmDetailsEntity

@Dao
interface FilmDetailsDao {
    @Query("SELECT * FROM film_details_entity WHERE slug = :slug")
    suspend fun getFilmDetails(slug: String): FilmDetailsEntity?

    @Query("SELECT * FROM film_details_entity WHERE slug IN (:slugs)")
    suspend fun getFilmDetailsBatch(slugs: List<String>): List<FilmDetailsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilmDetails(details: FilmDetailsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilmDetailsBatch(details: List<FilmDetailsEntity>)

    @Query("DELETE FROM film_details_entity")
    suspend fun clearFilmDetails()
}
