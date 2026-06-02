package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.result.Result

interface LibraryRepository {
    suspend fun getLibraries(): Result<List<Library>>
    suspend fun getLibrary(name: String): Result<Library?>
    suspend fun addToLibrary(library: Library): Boolean
    suspend fun createLibrary(library: Library): Boolean
    suspend fun removeFilmFromLibrary(libraryName: String, slug: String): Boolean
    suspend fun renameLibrary(oldName: String, newName: String): Boolean
    suspend fun deleteLibrary(name: String): Boolean
    suspend fun updatePosterUrl(name: String, posterUrl: String)
}
