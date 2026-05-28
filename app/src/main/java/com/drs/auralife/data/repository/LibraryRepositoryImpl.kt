package com.drs.auralife.data.repository

import com.drs.auralife.data.firebase.realtime.database.user.library.LibraryRepository as FirebaseLibraryRepository
import com.drs.auralife.data.mapper.FirebaseMapper.toDomainLibrary
import com.drs.auralife.data.mapper.FirebaseMapper.toDomainLibraries
import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.repository.LibraryRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LibraryRepositoryImpl : LibraryRepository {
    override suspend fun getLibraries(): List<Library> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseLibraryRepository.getLibrary { firebaseLibraries ->
                continuation.resume(firebaseLibraries.toDomainLibraries())
            }
        }
    }

    override suspend fun getLibrary(name: String): Library? {
        return suspendCancellableCoroutine { continuation ->
            FirebaseLibraryRepository.getLibrary { firebaseLibraries ->
                val library = firebaseLibraries.find { it.name == name }
                continuation.resume(library?.toDomainLibrary())
            }
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
}

