package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Banner
import com.drs.auralife.domain.repository.BannerRepository
import com.drs.auralife.domain.result.Result

class GetBannersUseCase @javax.inject.Inject constructor(
    private val bannerRepository: BannerRepository,
) {
    suspend operator fun invoke(): Result<List<Banner>> {
        return bannerRepository.getBanners()
    }
}
