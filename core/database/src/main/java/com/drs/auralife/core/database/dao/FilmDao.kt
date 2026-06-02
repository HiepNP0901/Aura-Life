package com.drs.auralife.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drs.auralife.core.database.entity.FilmEntity

@Dao
interface FilmDao {
    @Query("SELECT * FROM film_entity")
    suspend fun getAllFilms(): List<FilmEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFilms(films: List<FilmEntity>)

    @Query("DELETE FROM film_entity")
    suspend fun clearFilms()
}
