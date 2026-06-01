package com.drs.auralife.domain.repository

interface AvatarRepository {
    suspend fun getAvatar(): ByteArray?
    suspend fun uploadAvatar(imageBytes: ByteArray): Boolean
}
