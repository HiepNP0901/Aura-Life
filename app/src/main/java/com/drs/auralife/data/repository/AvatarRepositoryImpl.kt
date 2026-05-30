package com.drs.auralife.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.drs.auralife.core.utils.ImageEncoderDecoder
import com.drs.auralife.data.remote.firebase.Authentication
import com.drs.auralife.data.remote.firebase.AvatarDataSource as FirebaseAvatarRepository
import com.drs.auralife.domain.repository.AvatarRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AvatarRepositoryImpl @Inject constructor() : AvatarRepository {
    override suspend fun getAvatarUrl(): String? {
        return null
    }

    override suspend fun getAvatar(): Bitmap? {
        val userId = Authentication.getUserId() ?: return null
        return suspendCancellableCoroutine { continuation ->
            FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("avatar")
                .get()
                .addOnSuccessListener {
                    val value = it.value?.toString()
                    val bitmap = if (value != null) ImageEncoderDecoder.decodeFromBase64(value) else null
                    continuation.resume(bitmap)
                }
                .addOnFailureListener {
                    continuation.resume(null)
                }
        }
    }

    override suspend fun uploadAvatar(imageBytes: ByteArray): Boolean {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return false
        return suspendCancellableCoroutine { continuation ->
            FirebaseAvatarRepository.uploadAvatar(bitmap) { result ->
                continuation.resume(result.isSuccess)
            }
        }
    }
}
