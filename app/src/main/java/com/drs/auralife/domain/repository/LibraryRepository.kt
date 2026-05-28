package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.Library

interface LibraryRepository {
    suspend fun getLibraries(): List<Library>
    suspend fun getLibrary(name: String): Library?
    suspend fun addToLibrary(library: Library): Boolean
    suspend fun removeFilmFromLibrary(libraryName: String, slug: String): Boolean
    suspend fun renameLibrary(oldName: String, newName: String): Boolean
}
