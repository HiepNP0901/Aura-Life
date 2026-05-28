package com.drs.auralife.data.repository

import com.drs.auralife.domain.repository.AvatarRepository

class AvatarRepositoryImpl : AvatarRepository {
    override suspend fun getAvatarUrl(): String? {
        TODO("Implement Firebase avatar URL retrieval")
    }

    override suspend fun uploadAvatar(imageBytes: ByteArray): Boolean {
        TODO("Implement Firebase avatar upload")
    }
}
