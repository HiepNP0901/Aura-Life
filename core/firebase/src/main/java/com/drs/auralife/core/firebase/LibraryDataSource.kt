package com.drs.auralife.core.firebase

import com.drs.auralife.core.firebase.model.library.FilmLibrary
import com.drs.auralife.core.firebase.model.library.Library
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LibraryDataSource @Inject constructor(
    database: FirebaseDatabase,
) {

    private fun userIdOrThrow(): String {
        return Authentication.getUserId() ?: throw IllegalStateException("User not authenticated")
    }

    private val userRef = database.getReference("users")
    private val libraryRef by lazy { userRef.child(userIdOrThrow()).child("library") }

    private fun snapshotToLibrary(snapshot: DataSnapshot): Library? {
        val name = snapshot.key ?: return null
        val posterUrl = snapshot.child("posterUrl").value?.toString() ?: return null
        val listFilm = snapshot.child("listFilm").children.mapNotNull {
            val slug = it.key ?: return@mapNotNull null
            val episode = it.value?.toString() ?: return@mapNotNull null
            FilmLibrary(slug = slug, episode = episode)
        }.toList()
        return Library(name = name, posterUrl = posterUrl, listFilm = listFilm)
    }

    suspend fun getLibrary(): List<Library> {
        val snapshot = libraryRef.get().await()
        return snapshot.children.mapNotNull { snapshotToLibrary(it) }
    }

    suspend fun getLibraryData(name: String): Library? {
        val snapshot = libraryRef.get().await()
        val child = snapshot.child(name)
        return if (child.value != null) snapshotToLibrary(child) else null
    }

    suspend fun addLibraryData(
        nameLibrary: String,
        posterUrl: String,
        slug: String,
        episode: String,
    ): Boolean {
        return try {
            val snapshot = libraryRef.get().await()
            if (snapshot.child(nameLibrary).child("listFilm").child(slug).value != null) return false
            libraryRef.child(nameLibrary).child("posterUrl").ref.setValue(posterUrl).await()
            libraryRef.child(nameLibrary).child("listFilm").child(slug).ref.setValue(episode).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createLibrary(
        nameLibrary: String,
        posterUrl: String,
        slug: String,
        episode: String,
    ): Boolean {
        return try {
            val snapshot = libraryRef.get().await()
            if (snapshot.child(nameLibrary).value != null) return false
            libraryRef.child(nameLibrary).child("posterUrl").ref.setValue(posterUrl).await()
            libraryRef.child(nameLibrary).child("listFilm").child(slug).ref.setValue(episode).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeFilmFromLibrary(name: String, slug: String): Boolean {
        return try {
            libraryRef.child(name).child("listFilm").child(slug).ref.removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteLibrary(name: String): Boolean {
        return try {
            libraryRef.child(name).ref.removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun renameLibrary(oldName: String, newName: String): Boolean {
        return try {
            val snapshot = libraryRef.get().await()
            if (snapshot.child(newName).value != null) {
                throw Exception("Tên thư viện đã tồn tại!")
            }
            val data = snapshot.child(oldName).value
            libraryRef.child(newName).ref.setValue(data).await()
            libraryRef.child(oldName).ref.removeValue().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updatePosterUrl(name: String, posterUrl: String) {
        libraryRef.child(name).child("posterUrl").ref.setValue(posterUrl).await()
    }

    suspend fun updateEpisode(name: String, slug: String, episode: String) {
        libraryRef.child(name).child("listFilm").child(slug).ref.setValue(episode).await()
    }
}
