package com.drs.auralife.domain.repository

interface BannerRepository {
    suspend fun getBanners(): List<Pair<String, String>>
}
