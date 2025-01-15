package com.drs.auralife.data.firebase

import android.graphics.Bitmap
import com.drs.auralife.utils.ImageEncoderDecoder
import com.google.firebase.database.FirebaseDatabase

class RealtimeDB {
    companion object{
        private val database = FirebaseDatabase.getInstance()

        private val bannerRef = database.getReference("banners")

        val userRef = database.getReference("users")

        fun uploadAvatar(bitmap: Bitmap, callback: (Result<Boolean>) -> Unit) {
            val base64String = ImageEncoderDecoder.encodeToBase64(bitmap)
            val userId = Authentication.getUserId()

            userId.let {
                userRef.child(it.toString()).child("avatar").setValue(base64String)
                    .addOnSuccessListener {
                        callback(Result.success(true))
                    }
                    .addOnFailureListener { e ->
                        callback(Result.failure(Exception(e)))
                    }
            }
        }


        fun getAvatar(callback: (Bitmap) -> Unit) {
            val userId = Authentication.getUserId()

            userId.let {
                userRef.child(it.toString()).child("avatar").get().addOnSuccessListener {
                    ImageEncoderDecoder
                        .decodeFromBase64(it.value.toString())
                        ?.let { bitmap ->
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
    }
}