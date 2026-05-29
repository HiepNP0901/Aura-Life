package com.drs.auralife.data.remote.firebase

import com.drs.auralife.data.remote.firebase.model.library.FilmLibrary
import com.drs.auralife.data.remote.firebase.model.library.Library
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

object LibraryDataSource {
    private val userRef = FirebaseDatabase.getInstance().getReference("users")
    private val libraryRef = userRef.child(Authentication.getUserId().toString()).child("library")
    private var librarySnapshot: DataSnapshot? = null
    private val libraryData get() = librarySnapshot!!

    init {
        libraryRef.get().addOnSuccessListener {
            librarySnapshot = it
        }
    }

    fun recheckLibrary() {
        libraryRef.get().addOnSuccessListener {
            librarySnapshot = it
        }
    }

    fun addLibraryData(
        nameLibrary: String,
        posterUrl: String,
        slug: String,
        episode: String,
        callback: (Result<Boolean>) -> Unit,
    ) {
        if (libraryData
                .child(nameLibrary)
                .child("listFilm")
                .child(slug)
                .value != null
        ) {
            callback(Result.success(false))
        } else {
            libraryData.child(nameLibrary).apply {
                child("posterUrl")
                    .ref
                    .setValue(posterUrl)
                    .addOnSuccessListener {
                        child("listFilm")
                            .child(slug)
                            .ref
                            .setValue(episode)
                            .addOnSuccessListener {
                                recheckLibrary()
                                callback(Result.success(true))
                            }.addOnFailureListener { e ->
                                callback(Result.failure(Exception(e)))
                            }
                    }.addOnFailureListener { e ->
                        callback(Result.failure(Exception(e)))
                    }
            }
        }
    }

    fun createLibrary(
        nameLibrary: String,
        posterUrl: String,
        slug: String,
        episode: String,
        callback: (Result<Boolean>) -> Unit,
    ) {
        if (libraryData.child(nameLibrary).value != null) {
            callback(Result.success(false))
        } else {
            libraryData.child(nameLibrary).apply {
                child("posterUrl")
                    .ref
                    .setValue(posterUrl)
                    .addOnSuccessListener {
                        child("listFilm")
                            .child(slug)
                            .ref
                            .setValue(episode)
                            .addOnSuccessListener {
                                recheckLibrary()
                                callback(Result.success(true))
                            }.addOnFailureListener { e ->
                                callback(Result.failure(Exception(e)))
                            }
                    }.addOnFailureListener { e ->
                        callback(Result.failure(Exception(e)))
                    }
            }
        }
    }

    fun getLibrary(onDataReceived: (MutableList<Library>) -> Unit) {
        fun run(children: MutableIterable<DataSnapshot>) {
            val libraryList = mutableListOf<Library>()
            for (snapshot in children) {
                libraryList.add(
                    Library(
                        snapshot.key.toString(),
                        snapshot.child("posterUrl").value.toString(),
                        snapshot
                            .child("listFilm")
                            .children
                            .map {
                                FilmLibrary(it.key.toString(), it.value.toString())
                            }.toMutableList(),
                    ),
                )
            }
            onDataReceived(libraryList)
        }

        if (librarySnapshot == null) {
            libraryRef
                .get()
                .addOnSuccessListener {
                    librarySnapshot = it
                    run(libraryData.children)
                }.addOnFailureListener { _ ->
                    onDataReceived(MutableList(0) { Library("", "", mutableListOf()) })
                }
        } else {
            run(libraryData.children)
        }
    }

    fun getLibraryData(
        name: String,
        callback: (Library) -> Unit,
    ) {
        callback(snapshotToLibrary(libraryData.child(name)))
    }

    fun removeFilmFromLibrary(
        name: String,
        slug: String,
        callback: (Result<Boolean>) -> Unit,
    ) {
        libraryData
            .child(name)
            .child("listFilm")
            .child(slug)
            .ref
            .removeValue()
            .addOnSuccessListener {
                recheckLibrary()
                callback(Result.success(true))
            }.addOnFailureListener { e ->
                callback(Result.failure(Exception(e)))
            }
    }

    fun deleteLibrary(
        name: String,
        callback: (Result<Boolean>) -> Unit,
    ) {
        libraryData
            .child(name)
            .ref
            .removeValue()
            .addOnSuccessListener {
                recheckLibrary()
                callback(Result.success(true))
            }.addOnFailureListener { e ->
                callback(Result.failure(Exception(e)))
            }
    }

    fun renameLibrary(
        oldName: String,
        newName: String,
        callback: (Result<Boolean>) -> Unit,
    ) {
        libraryData.apply {
            if (child(newName).value != null) {
                callback(Result.failure(Exception("Tên thư viện đã tồn tại!")))
            } else {
                child(newName)
                    .ref
                    .setValue(child(oldName).value)
                    .addOnSuccessListener {
                        child(oldName)
                            .ref
                            .removeValue()
                            .addOnSuccessListener {
                                recheckLibrary()
                                callback(Result.success(true))
                            }.addOnFailureListener { e ->
                                callback(Result.failure(Exception(e)))
                            }
                    }.addOnFailureListener { e ->
                        callback(Result.failure(Exception(e)))
                    }
            }
        }
    }

    fun updatePosterUrl(
        name: String,
        posterUrl: String,
    ) {
        libraryData.child(name).child("posterUrl").ref.setValue(posterUrl).addOnSuccessListener {
            recheckLibrary()
        }
    }

    fun updateEpisode(
        name: String,
        slug: String,
        episode: String,
    ) {
        libraryData
            .child(name)
            .child("listFilm")
            .child(slug)
            .ref
            .setValue(episode)
            .addOnSuccessListener {
                recheckLibrary()
            }
    }

    fun snapshotToLibrary(snapshot: DataSnapshot): Library =
        Library(
            snapshot.key.toString(),
            snapshot.child("posterUrl").value.toString(),
            snapshot
                .child("listFilm")
                .children
                .map {
                    FilmLibrary(it.key.toString(), it.value.toString())
                }.toMutableList(),
        )
}
