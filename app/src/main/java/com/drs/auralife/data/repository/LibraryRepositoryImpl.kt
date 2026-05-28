package com.drs.auralife.data.repository

import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.repository.LibraryRepository

class LibraryRepositoryImpl : LibraryRepository {
    override suspend fun getLibraries(): List<Library> {
        TODO("Map Firebase library data to domain model")
    }

    override suspend fun getLibrary(name: String): Library? {
        TODO("Map Firebase library data to domain model")
    }

    override suspend fun addToLibrary(library: Library): Boolean {
        TODO("Implement library add logic")
    }

    override suspend fun removeFilmFromLibrary(libraryName: String, slug: String): Boolean {
        TODO("Implement library remove logic")
    }

    override suspend fun renameLibrary(oldName: String, newName: String): Boolean {
        TODO("Implement rename library logic")
    }
}
