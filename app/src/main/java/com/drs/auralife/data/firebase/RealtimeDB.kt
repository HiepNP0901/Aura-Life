package com.drs.auralife.data.firebase

import android.content.Context
import android.graphics.Bitmap
import com.drs.auralife.utils.ImageEncoderDecoder
import com.drs.auralife.utils.Time
import com.google.firebase.database.FirebaseDatabase
import java.time.Instant

object RealtimeDB {
    private val database = FirebaseDatabase.getInstance()
    private val bannerRef = database.getReference("banners")
    private val categoryRef = database.getReference("categories")
    val userRef = database.getReference("users")

    fun uploadAvatar(bitmap: Bitmap, callback: (Result<Boolean>) -> Unit) {
        val base64String = ImageEncoderDecoder.encodeToBase64(bitmap)
        val userId = Authentication.getUserId()
        userId.let {
            userRef.child(it.toString()).child("avatar").setValue(base64String)
                .addOnSuccessListener {
                    callback(Result.success(true))
                }.addOnFailureListener { e ->
                    callback(Result.failure(Exception(e)))
                }
        }
    }

    fun getAvatar(callback: (Bitmap) -> Unit) {
        val userId = Authentication.getUserId()
        userId.let {
            userRef.child(it.toString()).child("avatar").get().addOnSuccessListener {
                ImageEncoderDecoder.decodeFromBase64(it.value.toString())?.let { bitmap ->
                        callback(bitmap)
                    }
            }
        }
    }

    fun getBannerData(onDataReceived: (List<Pair<String, String>>) -> Unit) {
        bannerRef.get().addOnSuccessListener {
            val bannerList = mutableListOf<Pair<String, String>>()
            for (snapshot in it.children) {
                val bannerData = snapshot.getValue(String::class.java)
                bannerData?.let { bannerList.add(Pair(snapshot.key.toString(), it)) }
            }
            onDataReceived(bannerList)
        }
    }

    fun getCategoryData(onDataReceived: (List<Category>) -> Unit) {
        categoryRef.get().addOnSuccessListener {
            val categoryList = mutableListOf<Category>()
            for (snapshot in it.children) {
                val categoryData = Category(
                    snapshot.key.toString(),
                    snapshot.child("en").value.toString(),
                    snapshot.child("vi").value.toString()
                )
                categoryData.let { categoryList.add(it) }
            }
            onDataReceived(categoryList)
        }.addOnFailureListener {
            onDataReceived(emptyList())
        }
    }

    fun getHistoryData(context: Context, onDataReceived: (List<History>) -> Unit) {
        val userId = Authentication.getUserId()

        userId.let {
            userRef.child(it.toString()).child("history").get().addOnSuccessListener {
                val historyList = mutableListOf<History>()
                for (snapshot in it.children) {
                    var date =
                        Instant.ofEpochMilli(snapshot.child("date").value.toString().toLong())
                    val historyData = History(
                        snapshot.child("slug").value.toString(),
                        snapshot.child("episode").value.toString().toInt(),
                        snapshot.child("position").value.toString().toLong(),
                        Time.calculateTimeDifference(date, context)
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
}