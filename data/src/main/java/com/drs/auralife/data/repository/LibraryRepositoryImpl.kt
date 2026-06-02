package com.drs.auralife.data.repository

import com.drs.auralife.core.database.dao.LibraryDao
import com.drs.auralife.core.database.dao.LibraryFilmCrossRefDao
import com.drs.auralife.core.database.mapper.LocalMapper.toDomainLibrary
import com.drs.auralife.core.database.mapper.LocalMapper.toLibraryEntity
import com.drs.auralife.core.database.mapper.LocalMapper.toLibraryFilmCrossRefs
import com.drs.auralife.data.remote.firebase.FirebaseMapper.toDomainLibraries
import com.drs.auralife.data.remote.firebase.FirebaseMapper.toDomainLibrary
import com.drs.auralife.data.remote.firebase.LibraryDataSource
import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.repository.LibraryRepository
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val libraryDao: LibraryDao,
    private val libraryFilmCrossRefDao: LibraryFilmCrossRefDao,
    private val libraryDataSource: LibraryDataSource,
) : LibraryRepository {
    override suspend fun getLibraries(): List<Library> {
        return try {
            val libraries = libraryDataSource.getLibrary().toDomainLibraries()
            libraryDao.clear()
            libraryFilmCrossRefDao.clear()
            libraries.forEach { lib ->
                libraryDao.insertLibrary(lib.toLibraryEntity())
                libraryFilmCrossRefDao.insertAll(lib.toLibraryFilmCrossRefs())
            }
            libraries
        } catch (e: Exception) {
            libraryDao.getAllWithFilms().map { it.toDomainLibrary() }
        }
    }

    override suspend fun getLibrary(name: String): Library? {
        return try {
            val library = libraryDataSource.getLibraryData(name)?.toDomainLibrary()
            if (library != null) {
                libraryDao.insertLibrary(library.toLibraryEntity())
                libraryFilmCrossRefDao.insertAll(library.toLibraryFilmCrossRefs())
            }
            library
        } catch (e: Exception) {
            libraryDao.getAllWithFilms().find { it.library.name == name }?.toDomainLibrary()
        }
    }

    override suspend fun addToLibrary(library: Library): Boolean {
        if (library.films.isEmpty()) return false
        val film = library.films.first()
        return libraryDataSource.addLibraryData(library.name, library.posterUrl, film.slug, film.currentEpisode)
    }

    override suspend fun removeFilmFromLibrary(libraryName: String, slug: String): Boolean {
        return libraryDataSource.removeFilmFromLibrary(libraryName, slug)
    }

    override suspend fun renameLibrary(oldName: String, newName: String): Boolean {
        return libraryDataSource.renameLibrary(oldName, newName)
    }

    override suspend fun createLibrary(library: Library): Boolean {
        if (library.films.isEmpty()) return false
        val film = library.films.first()
        return libraryDataSource.createLibrary(library.name, library.posterUrl, film.slug, film.currentEpisode)
    }

    override suspend fun deleteLibrary(name: String): Boolean {
        return libraryDataSource.deleteLibrary(name)
    }

    override suspend fun updatePosterUrl(name: String, posterUrl: String) {
        libraryDataSource.updatePosterUrl(name, posterUrl)
    }
}
