package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.Banner

interface BannerRepository {
    suspend fun getBanners(): List<Banner>
}
