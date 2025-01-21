package com.drs.auralife.data.firebase.library

import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.RealtimeDB
import com.google.firebase.database.DataSnapshot

object LibraryRepository {
    private val userRef = RealtimeDB.userRef

    fun addLibraryData(
        name: String,
        posterUrl: String,
        slug: String,
        episode: String,
        callback: (Result<Boolean>) -> Unit
    ) {
        val userId = Authentication.getUserId()

        userId.let { id ->
            val library = userRef.child(id.toString()).child("library").child(name)
            library.get().addOnSuccessListener {
                if (it.exists()) {
                    val libraryData = snapshotToLibrary(it)
                    if (libraryData.listFilm.any { film -> film.slug == slug }) {
                        callback(Result.success(false))
                    }
                    else {
                        library.child("listFilm").push()
                            .setValue(FilmLibrary(slug, episode))
                        callback(Result.success(true))
                    }
                    library.child("posterUrl").setValue(posterUrl)
                }
                else {
                    library.child("posterUrl").setValue(posterUrl)
                    library.child("listFilm").push()
                        .setValue(FilmLibrary(slug, episode))
                }
            }.addOnFailureListener { e ->
                callback(Result.failure(Exception(e)))
            }
        }
    }


    fun getLibrary(onDataReceived: (MutableList<Library>) -> Unit) {
        val userId = Authentication.getUserId()

        userId.let { id ->
            val listLibrary = userRef.child(id.toString()).child("library")
            listLibrary.get().addOnSuccessListener {
                val libraryList = mutableListOf<Library>()
                for (snapshot in it.children) {
                    val libraryData = Library(
                        snapshot.key.toString(),
                        snapshot.child("posterUrl").value.toString(),
                        snapshot.child("listFilm").children.map {
                            FilmLibrary(
                                it.child("slug").value.toString(),
                                it.child("episode").value.toString()
                            )
                        }.toMutableList()
                    )
                    libraryData.let { libraryList.add(it) }
                }
                onDataReceived(libraryList)
            }.addOnFailureListener {
                onDataReceived(mutableListOf())
            }
        }
    }



    fun getLibraryData(name: String, callback: (Library) -> Unit) {
        val userId = Authentication.getUserId()

        userId.let { id ->
            val library = userRef.child(id.toString()).child("library").child(name)
            library.get().addOnSuccessListener {
                val libraryData = snapshotToLibrary(it)
                callback(libraryData)
            }.addOnFailureListener { _ ->
                callback(Library("", "", mutableListOf()))
            }
        }
    }


    fun removeFilmFromLibrary(name: String, slug: String, callback: (Result<Boolean>) -> Unit) {
        val userId = Authentication.getUserId()

        userId.let { id ->
            val library = userRef.child(id.toString()).child("library").child(name)
            library.get().addOnSuccessListener {
                val listFilm = it.child("listFilm").children
                for (film in listFilm) {
                    if (film.child("slug").value.toString() == slug) {
                        film.ref.removeValue().addOnSuccessListener {
                            callback(Result.success(true))
                        }.addOnFailureListener { e ->
                            callback(Result.failure(Exception(e)))
                        }
                    }
                    else {
                        callback(Result.success(false))
                    }
                }
            }.addOnFailureListener { e ->
                callback(Result.failure(Exception(e)))
            }
        }
    }

    fun deleteLibrary(name: String, callback: (Result<Boolean>) -> Unit) {
        val userId = Authentication.getUserId()

        userId.let { id ->
            val library = userRef.child(id.toString()).child("library").child(name)
            library.removeValue().addOnSuccessListener {
                callback(Result.success(true))
            }.addOnFailureListener { e ->
                callback(Result.failure(Exception(e)))
            }
        }
    }

    fun renameLibrary(oldName: String, newName: String, callback: (Result<Boolean>) -> Unit) {
        val userId = Authentication.getUserId()

        userId.let {
            val listLibrary = userRef.child(it.toString()).child("library")

            listLibrary.child(oldName).get().addOnSuccessListener {
                val libraryData = snapshotToLibrary(it)
                libraryData.name = newName
                listLibrary.child(newName).setValue(libraryData)
                listLibrary.child(oldName).removeValue()
                callback(Result.success(true))
            }.addOnFailureListener { e ->
                callback(Result.failure(Exception(e)))
            }
        }
    }

    fun updatePosterUrl(name: String, posterUrl: String){
        val userId = Authentication.getUserId()

        userId.let {
            userRef.child(it.toString()).child("library").child(name).child("posterUrl")
                .setValue(posterUrl)
        }
    }

    fun updateEpisode(name: String, slug: String, episode: String) {
        val userId = Authentication.getUserId()

        userId.let {
            val library = userRef.child(it.toString()).child("library").child(name)
            library.get().addOnSuccessListener {
                val listLibrary = snapshotToLibrary(it)
                listLibrary.listFilm.forEach { film ->
                    if (film.slug == slug) {
                        film.episode = episode
                    }
                }
                library.setValue(listLibrary)
            }
        }
    }

    fun snapshotToLibrary(snapshot: DataSnapshot): Library {
        return Library(
            snapshot.key.toString(),
            snapshot.child("posterUrl").value.toString(),
            snapshot.child("listFilm").children.map {
                FilmLibrary(
                    it.child("slug").value.toString(), it.child("episode").value.toString()
                )
            }.toMutableList()
        )
    }
}