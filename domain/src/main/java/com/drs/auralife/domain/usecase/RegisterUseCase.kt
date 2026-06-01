package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(username: String, password: String): Result<String> {
        return authRepository.register(username, password)
    }
}
