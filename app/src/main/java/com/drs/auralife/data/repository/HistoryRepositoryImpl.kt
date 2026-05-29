package com.drs.auralife.data.repository

import com.drs.auralife.data.local.dao.HistoryDao
import com.drs.auralife.data.local.mapper.LocalMapper.toDomainHistoryItem
import com.drs.auralife.data.local.mapper.LocalMapper.toHistoryEntity
import com.drs.auralife.data.remote.firebase.Authentication
import com.drs.auralife.data.remote.firebase.FirebaseMapper.toDomainHistoryItems
import com.drs.auralife.data.remote.firebase.HistoryDataSource as FirebaseHistoryRepository
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.repository.HistoryRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao,
) : HistoryRepository {
    override suspend fun getHistory(): List<HistoryItem> {
        return try {
            val history = suspendCancellableCoroutine<List<HistoryItem>> { continuation ->
                FirebaseHistoryRepository.getHistoryData { firebaseHistory ->
                    continuation.resume(firebaseHistory.toDomainHistoryItems())
                }
            }
            historyDao.clear()
            history.forEach { historyDao.insertHistory(it.toHistoryEntity()) }
            history
        } catch (e: Exception) {
            historyDao.getAll().map { it.toDomainHistoryItem() }
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
