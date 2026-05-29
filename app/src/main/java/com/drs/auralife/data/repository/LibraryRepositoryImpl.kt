package com.drs.auralife.data.repository

import com.drs.auralife.data.local.dao.LibraryDao
import com.drs.auralife.data.local.dao.LibraryFilmCrossRefDao
import com.drs.auralife.data.local.mapper.LocalMapper.toDomainLibrary
import com.drs.auralife.data.local.mapper.LocalMapper.toLibraryEntity
import com.drs.auralife.data.local.mapper.LocalMapper.toLibraryFilmCrossRefs
import com.drs.auralife.data.remote.firebase.FirebaseMapper.toDomainLibraries
import com.drs.auralife.data.remote.firebase.FirebaseMapper.toDomainLibrary as firebaseToDomainLibrary
import com.drs.auralife.data.remote.firebase.LibraryDataSource as FirebaseLibraryRepository
import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.repository.LibraryRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject

class LibraryRepositoryImpl @Inject constructor(
    private val libraryDao: LibraryDao,
    private val libraryFilmCrossRefDao: LibraryFilmCrossRefDao,
) : LibraryRepository {
    override suspend fun getLibraries(): List<Library> {
        return try {
            val libraries = suspendCancellableCoroutine<List<Library>> { continuation ->
                FirebaseLibraryRepository.getLibrary { firebaseLibraries ->
                    continuation.resume(firebaseLibraries.toDomainLibraries())
                }
            }
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
            val library = suspendCancellableCoroutine<Library?> { continuation ->
                FirebaseLibraryRepository.getLibrary { firebaseLibraries ->
                    val lib = firebaseLibraries.find { it.name == name }
                    continuation.resume(lib?.firebaseToDomainLibrary())
                }
            }
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
        return suspendCancellableCoroutine { continuation ->
            if (library.films.isEmpty()) {
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }
            val film = library.films.first()
            FirebaseLibraryRepository.addLibraryData(
                library.name,
                library.posterUrl,
                film.slug,
                film.currentEpisode,
            ) { result ->
                continuation.resume(result.isSuccess)
            }
        }
    }

    override suspend fun removeFilmFromLibrary(libraryName: String, slug: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            FirebaseLibraryRepository.removeFilmFromLibrary(libraryName, slug) { result ->
                continuation.resume(result.isSuccess)
            }
        }
    }

    override suspend fun renameLibrary(oldName: String, newName: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            FirebaseLibraryRepository.renameLibrary(oldName, newName) { result ->
                continuation.resume(result.isSuccess)
            }
        }
    }

    override suspend fun createLibrary(library: Library): Boolean {
        return suspendCancellableCoroutine { continuation ->
            if (library.films.isEmpty()) {
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }
            val film = library.films.first()
            FirebaseLibraryRepository.createLibrary(
                library.name,
                library.posterUrl,
                film.slug,
                film.currentEpisode,
            ) { result ->
                continuation.resume(result.isSuccess)
            }
        }
    }

    override suspend fun deleteLibrary(name: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            FirebaseLibraryRepository.deleteLibrary(name) { result ->
                continuation.resume(result.isSuccess)
            }
        }
    }
}

