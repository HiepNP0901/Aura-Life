package com.drs.auralife.data.firebase.realtime.database.user

import android.graphics.Bitmap
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.utils.ImageEncoderDecoder
import com.google.firebase.database.FirebaseDatabase

object AvatarRepository {
    val userRef = FirebaseDatabase.getInstance().getReference("users")

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
}