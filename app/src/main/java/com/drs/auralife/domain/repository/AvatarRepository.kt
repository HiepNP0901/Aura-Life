package com.drs.auralife.domain.repository

interface AvatarRepository {
    suspend fun getAvatarUrl(): String?
    suspend fun uploadAvatar(imageBytes: ByteArray): Boolean
}
