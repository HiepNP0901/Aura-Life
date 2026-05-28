package com.drs.auralife.data.repository

import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.realtime.database.user.history.HistoryRepository as FirebaseHistoryRepository
import com.drs.auralife.data.mapper.FirebaseMapper.toDomainHistoryItems
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.repository.HistoryRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor() : HistoryRepository {
    override suspend fun getHistory(): List<HistoryItem> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseHistoryRepository.getHistoryData { firebaseHistory ->
                continuation.resume(firebaseHistory.toDomainHistoryItems())
            }
        }
    }

    override suspend fun deleteHistory(slug: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val userId = Authentication.getUserId()
            if (userId != null) {
                val userRef = FirebaseDatabase.getInstance().getReference("users")
                userRef
                    .child(userId)
                    .child("history")
                    .orderByChild("slug")
                    .equalTo(slug)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        snapshot.children.forEach { child ->
                            child.ref.removeValue()
                                .addOnSuccessListener {
                                    continuation.resume(true)
                                }
                                .addOnFailureListener {
                                    continuation.resume(false)
                                }
                        }
                    }
                    .addOnFailureListener {
                        continuation.resume(false)
                    }
            } else {
                continuation.resume(false)
            }
        }
    }

    override suspend fun addHistory(slug: String, episode: Int, position: Long): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val userId = Authentication.getUserId()
            if (userId != null) {
                val userRef = FirebaseDatabase.getInstance().getReference("users")
                userRef
                    .child(userId)
                    .child("history")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        for (child in snapshot.children) {
                            if (child.child("slug").value.toString() == slug) {
                                child.ref.removeValue()
                            }
                        }
                        val historyData = mapOf(
                            "slug" to slug,
                            "episode" to episode,
                            "position" to position,
                            "date" to System.currentTimeMillis().toString(),
                        )
                        userRef
                            .child(userId)
                            .child("history")
                            .push()
                            .setValue(historyData)
                            .addOnSuccessListener {
                                continuation.resume(true)
                            }
                            .addOnFailureListener {
                                continuation.resume(false)
                            }
                    }
                    .addOnFailureListener {
                        continuation.resume(false)
                    }
            } else {
                continuation.resume(false)
            }
        }
    }
}
