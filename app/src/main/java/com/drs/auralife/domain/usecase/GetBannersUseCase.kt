package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.BannerRepository

class GetBannersUseCase(
    private val bannerRepository: BannerRepository,
) {
    suspend operator fun invoke(): List<String> {
        return bannerRepository.getBanners()
    }
}
