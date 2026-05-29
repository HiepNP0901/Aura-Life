package com.drs.auralife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.drs.auralife.data.local.entity.LibraryEntity
import com.drs.auralife.data.local.entity.LibraryFilmCrossRef

@Dao
interface LibraryDao {
    @Transaction
    @Query("SELECT * FROM library_entity")
    suspend fun getAllWithFilms(): List<com.drs.auralife.data.local.entity.LibraryWithFilms>

    @Query("SELECT * FROM library_entity WHERE name = :name")
    suspend fun getLibrary(name: String): LibraryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibrary(library: LibraryEntity)

    @Query("DELETE FROM library_entity")
    suspend fun clear()
}
