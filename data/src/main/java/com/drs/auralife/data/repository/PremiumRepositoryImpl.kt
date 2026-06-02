package com.drs.auralife.data.repository

import com.drs.auralife.core.firebase.FirebaseMapper.toDomainPremiumStatus
import com.drs.auralife.core.firebase.PremiumDataSource
import com.drs.auralife.domain.model.PremiumStatus
import com.drs.auralife.domain.repository.PremiumRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class PremiumRepositoryImpl @Inject constructor(
    private val premiumDataSource: PremiumDataSource,
) : PremiumRepository {
    override suspend fun getPremiumStatus(): PremiumStatus {
        return suspendCancellableCoroutine { continuation ->
            premiumDataSource.getPremiumStatus { firebasePremium ->
                continuation.resume(firebasePremium.toDomainPremiumStatus())
            }
        }
    }

    override suspend fun setPremium(months: Int): Boolean {
        return suspendCancellableCoroutine { continuation ->
            premiumDataSource.uploadPremium(months) { result ->
                continuation.resume(result.getOrDefault(false))
            }
        }
    }
}
