package com.drs.auralife.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.drs.auralife.core.database.entity.LibraryFilmCrossRef

@Dao
interface LibraryFilmCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crossRefs: List<LibraryFilmCrossRef>)

    @Query("DELETE FROM library_film_cross_ref WHERE libraryName = :libraryName")
    suspend fun deleteByLibrary(libraryName: String)

    @Query("DELETE FROM library_film_cross_ref")
    suspend fun clear()
}
