package com.drs.auralife.data.firebase.realtime.database.user.history

import com.drs.auralife.data.firebase.Authentication
import com.google.firebase.database.FirebaseDatabase

object HistoryRepository {
    val userRef = FirebaseDatabase.getInstance().getReference("users")

    fun getHistoryData(onDataReceived: (List<History>) -> Unit) {
        val userId = Authentication.getUserId()

        userId.let {
            userRef.child(it.toString()).child("history").get().addOnSuccessListener {
                val historyList = mutableListOf<History>()
                for (snapshot in it.children) {
                    val historyData = History(
                        snapshot.child("slug").value.toString(),
                        snapshot.child("episode").value.toString().toInt(),
                        snapshot.child("position").value.toString().toLong(),
                        snapshot.child("date").value.toString()
                    )
                    historyData.let { historyList.add(it) }
                }
                onDataReceived(historyList)
            }.addOnFailureListener {
                onDataReceived(emptyList())
            }
        }
    }

    fun addHistoryData(slug: String, episode: Int, position: Long) {
        val userId = Authentication.getUserId()

        userId.let {
            val history = userRef.child(it.toString()).child("history")
            history.get().addOnSuccessListener {
                for (snapshot in it.children) {
                    if (snapshot.child("slug").value.toString() == slug) {
                        snapshot.ref.removeValue()
                    }
                }
                val historyData =
                    History(slug, episode, position, System.currentTimeMillis().toString())
                history.push().setValue(historyData)
            }
        }
    }

    fun deleteHistory(slug: String) {
        val userId = Authentication.getUserId()
        userId.let {
            userRef.child(it.toString()).child("history").get().addOnSuccessListener {
                for (snapshot in it.children) {
                    if (snapshot.child("slug").value.toString() == slug) {
                        snapshot.ref.removeValue()
                    }
                }
            }
        }
    }
}
