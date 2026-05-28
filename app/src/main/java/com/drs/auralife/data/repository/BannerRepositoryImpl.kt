package com.drs.auralife.data.repository

import com.drs.auralife.data.firebase.realtime.database.BannerRepository as FirebaseBannerRepository
import com.drs.auralife.domain.repository.BannerRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject

class BannerRepositoryImpl @Inject constructor() : BannerRepository {
    override suspend fun getBanners(): List<Pair<String, String>> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseBannerRepository.getBannerData { bannerPairs ->
                continuation.resume(bannerPairs)
            }
        }
    }
}
