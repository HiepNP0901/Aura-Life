package com.drs.auralife.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.drs.auralife.data.remote.firebase.AvatarDataSource
import com.drs.auralife.domain.repository.AvatarRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AvatarRepositoryImpl @Inject constructor(
    private val avatarDataSource: AvatarDataSource,
) : AvatarRepository {
    override suspend fun getAvatarUrl(): String? {
        return null
    }

    override suspend fun getAvatar(): Bitmap? {
        return suspendCancellableCoroutine { continuation ->
            avatarDataSource.getAvatar { bitmap ->
                continuation.resume(bitmap)
            }
        }
    }

    override suspend fun uploadAvatar(imageBytes: ByteArray): Boolean {
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: return false
        return suspendCancellableCoroutine { continuation ->
            avatarDataSource.uploadAvatar(bitmap) { result ->
                continuation.resume(result.isSuccess)
            }
        }
    }
}
