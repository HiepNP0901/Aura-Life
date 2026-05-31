package com.drs.auralife.domain.repository

import android.graphics.Bitmap

interface AvatarRepository {
    suspend fun getAvatarUrl(): String?
    suspend fun getAvatar(): Bitmap?
    suspend fun uploadAvatar(imageBytes: ByteArray): Boolean
}
