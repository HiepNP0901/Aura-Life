package com.drs.auralife.data.remote.firebase

import com.drs.auralife.data.remote.firebase.model.history.History
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

object HistoryDataSource {
    private val userRef = FirebaseDatabase.getInstance().getReference("users")

    suspend fun getHistoryData(): List<History> {
        val userId = Authentication.getUserId() ?: return emptyList()
        val snapshot = userRef.child(userId).child("history").get().await()
        return snapshot.children.map { snap ->
            History(
                slug = snap.child("slug").value.toString(),
                episode = snap.child("episode").value.toString().toInt(),
                position = snap.child("position").value.toString().toLong(),
                date = snap.child("date").value.toString(),
            )
        }
    }

    suspend fun addHistoryData(slug: String, episode: Int, position: Long): Boolean {
        val userId = Authentication.getUserId() ?: return false
        return try {
            val historyRef = userRef.child(userId).child("history")
            val snapshot = historyRef.get().await()
            for (snap in snapshot.children) {
                if (snap.child("slug").value.toString() == slug) {
                    snap.ref.removeValue().await()
                }
            }
            val historyData = History(slug, episode, position, System.currentTimeMillis().toString())
            historyRef.push().setValue(historyData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteHistory(slug: String): Boolean {
        val userId = Authentication.getUserId() ?: return false
        return try {
            val snapshot = userRef.child(userId).child("history").get().await()
            for (snap in snapshot.children) {
                if (snap.child("slug").value.toString() == slug) {
                    snap.ref.removeValue().await()
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
