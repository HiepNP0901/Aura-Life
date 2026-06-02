package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.Banner
import com.drs.auralife.domain.result.Result

interface BannerRepository {
    suspend fun getBanners(): Result<List<Banner>>
}
