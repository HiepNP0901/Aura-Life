package com.drs.auralife.data.firebase.library

import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.RealtimeDB

class LibraryRepository {
    private val userRef = RealtimeDB.userRef

    fun addLibraryData(
        name: String, posterUrl: String, slug: String, callback: (Result<Boolean>) -> Unit
    ) {
        val userId = Authentication.getUserId()

        userId.let { id ->
            val library = userRef.child(id.toString()).child("library").child(name)
            library.get().addOnSuccessListener {
                if (it.exists()) {
                    val listSlug = it.child("listSlug").children.map { it.value.toString() }
                    if (!listSlug.contains(slug)) {
                        val newListSlug = listSlug.toMutableList()
                        newListSlug.add(slug)
                        library.child("listSlug").setValue(newListSlug)
                        library.child("posterUrl").setValue(posterUrl)
                        callback(Result.success(true))
                    }
                    else {
                        callback(Result.success(false))
                    }
                }
                else {
                    val libraryData = Library(name, posterUrl, mutableListOf(slug))
                    library.setValue(libraryData)
                    callback(Result.success(true))
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
                    val libraryData = Library(snapshot.child("name").value.toString(),
                        snapshot.child("posterUrl").value.toString(),
                        snapshot.child("listSlug").children.map { it.value.toString() })
                    libraryData.let { libraryList.add(it) }
                }
                onDataReceived(libraryList)
            }
        }
    }


    fun getLibraryData(name: String, callback: (Library) -> Unit) {
        val userId = Authentication.getUserId()

        userId.let { id ->
            val library = userRef.child(id.toString()).child("library").child(name)
            library.get().addOnSuccessListener {
                val libraryData = Library(it.child("name").value.toString(),
                    it.child("posterUrl").value.toString(),
                    it.child("listSlug").children.map { it.value.toString() })
                callback(libraryData)
            }
        }
    }


    fun removeFilmFromLibrary(name: String, slug: String, callback: (Result<Boolean>) -> Unit) {
        val userId = Authentication.getUserId()

        userId.let { id ->
            val library = userRef.child(id.toString()).child("library").child(name)
            library.get().addOnSuccessListener {
                val listSlug = it.child("listSlug").children.map { it.value.toString() }
                val newListSlug = listSlug.toMutableList()
                newListSlug.remove(slug)
                if (newListSlug.isEmpty()) {
                    library.removeValue()
                }
                else {
                    library.child("listSlug").setValue(newListSlug)
                }
                callback(Result.success(true))
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
                val libraryData = Library(newName,
                    it.child("posterUrl").value.toString(),
                    it.child("listSlug").children.map { it.value.toString() })
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
            val library = userRef.child(it.toString()).child("library").child(name)
            library.child("posterUrl").setValue(posterUrl)
        }
    }
}