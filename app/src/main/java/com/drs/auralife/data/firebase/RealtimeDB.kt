package com.drs.auralife.data.firebase

import android.graphics.Bitmap
import com.drs.auralife.utils.ImageEncoderDecoder
import com.google.firebase.database.FirebaseDatabase

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
}