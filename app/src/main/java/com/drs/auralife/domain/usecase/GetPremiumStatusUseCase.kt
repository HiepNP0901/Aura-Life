package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.PremiumStatus
import com.drs.auralife.domain.repository.PremiumRepository

class GetPremiumStatusUseCase @javax.inject.Inject constructor(
    private val premiumRepository: PremiumRepository,
) {
    suspend operator fun invoke(): PremiumStatus {
        return premiumRepository.getPremiumStatus()
    }
}
