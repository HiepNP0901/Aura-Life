package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.PremiumRepository

class SetPremiumUseCase @javax.inject.Inject constructor(
    private val premiumRepository: PremiumRepository,
) {
    suspend operator fun invoke(months: Int): Boolean {
        return premiumRepository.setPremium(months)
    }
}
