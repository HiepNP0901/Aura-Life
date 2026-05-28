package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.AvatarRepository

class UploadAvatarUseCase(
    private val avatarRepository: AvatarRepository,
) {
    suspend operator fun invoke(imageBytes: ByteArray): Boolean {
        return avatarRepository.uploadAvatar(imageBytes)
    }
}
