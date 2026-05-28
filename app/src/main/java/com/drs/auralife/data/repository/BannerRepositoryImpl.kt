package com.drs.auralife.data.repository

import com.drs.auralife.domain.repository.BannerRepository

class BannerRepositoryImpl : BannerRepository {
    override suspend fun getBanners(): List<String> {
        TODO("Implement Firebase banner data retrieval and mapping")
    }
}
