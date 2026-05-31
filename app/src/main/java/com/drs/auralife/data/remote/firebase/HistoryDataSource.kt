package com.drs.auralife.data.remote.firebase

import com.drs.auralife.data.remote.firebase.model.history.History
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HistoryDataSource @Inject constructor(
    private val database: FirebaseDatabase,
) {
    private val userRef = database.getReference("users")

    suspend fun getHistoryData(): List<History> {
        val userId = Authentication.getUserId() ?: return emptyList()
        val snapshot = userRef.child(userId).child("history").get().await()
        return snapshot.children.mapNotNull { snap ->
            val slug = snap.child("slug").value?.toString() ?: return@mapNotNull null
            val episodeStr = snap.child("episode").value?.toString() ?: return@mapNotNull null
            val positionStr = snap.child("position").value?.toString() ?: return@mapNotNull null
            val date = snap.child("date").value?.toString() ?: return@mapNotNull null
            History(
                slug = slug,
                episode = episodeStr.toIntOrNull() ?: return@mapNotNull null,
                position = positionStr.toLongOrNull() ?: return@mapNotNull null,
                date = date,
            )
        }
    }

    suspend fun addHistoryData(slug: String, episode: Int, position: Long): Boolean {
        val userId = Authentication.getUserId() ?: return false
        return try {
            val historyRef = userRef.child(userId).child("history")
            val snapshot = historyRef.get().await()
            for (snap in snapshot.children) {
                if (snap.child("slug").value?.toString() == slug) {
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
                if (snap.child("slug").value?.toString() == slug) {
                    snap.ref.removeValue().await()
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
