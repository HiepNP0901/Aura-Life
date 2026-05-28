package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.BannerRepository

class GetBannersUseCase @javax.inject.Inject constructor(
    private val bannerRepository: BannerRepository,
) {
    suspend operator fun invoke(): List<String> {
        return bannerRepository.getBanners()
    }
}
