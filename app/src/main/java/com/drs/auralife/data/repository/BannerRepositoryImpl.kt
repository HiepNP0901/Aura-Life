package com.drs.auralife.data.repository

import com.drs.auralife.data.remote.firebase.BannerDataSource as FirebaseBannerRepository
import com.drs.auralife.data.local.dao.BannerCacheDao
import com.drs.auralife.data.local.mapper.LocalMapper.toBannerCacheEntity
import com.drs.auralife.data.local.mapper.LocalMapper.toBannerPair
import com.drs.auralife.domain.repository.BannerRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject

class BannerRepositoryImpl @Inject constructor(
    private val bannerCacheDao: BannerCacheDao,
) : BannerRepository {
    override suspend fun getBanners(): List<Pair<String, String>> {
        return try {
            val banners = suspendCancellableCoroutine<List<Pair<String, String>>> { continuation ->
                FirebaseBannerRepository.getBannerData { bannerPairs ->
                    continuation.resume(bannerPairs)
                }
            }
            bannerCacheDao.clear()
            bannerCacheDao.insertAll(banners.map { it.toBannerCacheEntity() })
            banners
        } catch (e: Exception) {
            bannerCacheDao.getAll().map { it.toBannerPair() }
        }
    }
}
