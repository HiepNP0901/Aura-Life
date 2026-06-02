package com.drs.auralife.data.repository

import com.drs.auralife.core.database.dao.BannerCacheDao
import com.drs.auralife.core.database.mapper.LocalMapper.toBannerCacheEntity
import com.drs.auralife.core.database.mapper.LocalMapper.toDomainBanner
import com.drs.auralife.core.firebase.BannerDataSource
import com.drs.auralife.core.firebase.FirebaseMapper.toDomainBanners
import com.drs.auralife.domain.model.Banner
import com.drs.auralife.domain.repository.BannerRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject

class BannerRepositoryImpl @Inject constructor(
    private val bannerCacheDao: BannerCacheDao,
    private val bannerDataSource: BannerDataSource,
) : BannerRepository {
    override suspend fun getBanners(): List<Banner> {
        return try {
            val banners = suspendCancellableCoroutine { continuation ->
                bannerDataSource.getBannerData { firebaseBanners ->
                    continuation.resume(firebaseBanners.toDomainBanners())
                }
            }
            bannerCacheDao.clear()
            bannerCacheDao.insertAll(banners.map { it.toBannerCacheEntity() })
            banners
        } catch (e: Exception) {
            bannerCacheDao.getAll().map { it.toDomainBanner() }
        }
    }
}
