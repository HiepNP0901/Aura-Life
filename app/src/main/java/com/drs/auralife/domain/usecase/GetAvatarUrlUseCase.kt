package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.AvatarRepository

class GetAvatarUrlUseCase(
    private val avatarRepository: AvatarRepository,
) {
    suspend operator fun invoke(): String? {
        return avatarRepository.getAvatarUrl()
    }
}
