package com.drs.auralife.domain.repository

import com.drs.auralife.domain.result.Result

interface AvatarRepository {
    suspend fun getAvatar(): Result<ByteArray?>
    suspend fun uploadAvatar(imageBytes: ByteArray): Boolean
}
