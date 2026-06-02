package com.drs.auralife.data.repository

import com.drs.auralife.core.database.dao.BannerCacheDao
import com.drs.auralife.core.database.mapper.LocalMapper.toBannerCacheEntity
import com.drs.auralife.core.database.mapper.LocalMapper.toDomainBanner
import com.drs.auralife.core.firebase.BannerDataSource
import com.drs.auralife.core.firebase.FirebaseMapper.toDomainBanners
import com.drs.auralife.domain.model.Banner
import com.drs.auralife.domain.repository.BannerRepository
import com.drs.auralife.domain.result.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class BannerRepositoryImpl @Inject constructor(
    private val bannerCacheDao: BannerCacheDao,
    private val bannerDataSource: BannerDataSource,
) : BannerRepository {
    override suspend fun getBanners(): Result<List<Banner>> {
        return try {
            val banners = suspendCancellableCoroutine { continuation ->
                bannerDataSource.getBannerData { firebaseBanners ->
                    continuation.resume(firebaseBanners.toDomainBanners())
                }
            }
            bannerCacheDao.clear()
            bannerCacheDao.insertAll(banners.map { it.toBannerCacheEntity() })
            Result.Success(banners)
        } catch (e: Exception) {
            val cached = bannerCacheDao.getAll().map { it.toDomainBanner() }
            if (cached.isNotEmpty()) Result.Success(cached) else Result.Error(e)
        }
    }
}
