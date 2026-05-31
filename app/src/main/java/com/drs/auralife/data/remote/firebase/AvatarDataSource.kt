package com.drs.auralife.data.remote.firebase

import android.graphics.Bitmap
import com.drs.auralife.core.util.ImageEncoderDecoder
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class AvatarDataSource @Inject constructor(
    database: FirebaseDatabase,
) {
    private val userRef = database.getReference("users")

    fun uploadAvatar(
        bitmap: Bitmap,
        callback: (Result<Boolean>) -> Unit,
    ) {
        val base64String = ImageEncoderDecoder.encodeToBase64(bitmap)
        val userId = Authentication.getUserId() ?: return callback(Result.failure(Exception("User not authenticated")))
        userRef
            .child(userId)
            .child("avatar")
            .setValue(base64String)
            .addOnSuccessListener {
                callback(Result.success(true))
            }.addOnFailureListener { e ->
                callback(Result.failure(Exception(e)))
            }
    }

    fun getAvatar(callback: (Bitmap) -> Unit) {
        val userId = Authentication.getUserId() ?: return
        userRef.child(userId).child("avatar").get().addOnSuccessListener {
            ImageEncoderDecoder.decodeFromBase64(it.value.toString())?.let { bitmap ->
                callback(bitmap)
            }
        }
    }
}
