package com.drs.auralife.data.repository

import com.drs.auralife.data.remote.firebase.FirebaseMapper.toDomainPremiumStatus
import com.drs.auralife.data.remote.firebase.PremiumDataSource
import com.drs.auralife.domain.model.PremiumStatus
import com.drs.auralife.domain.repository.PremiumRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class PremiumRepositoryImpl @Inject constructor() : PremiumRepository {
    override suspend fun getPremiumStatus(): PremiumStatus {
        return suspendCancellableCoroutine { continuation ->
            PremiumDataSource.getPremiumStatus { firebasePremium ->
                continuation.resume(firebasePremium.toDomainPremiumStatus())
            }
        }
    }

    override suspend fun setPremium(months: Int): Boolean {
        return suspendCancellableCoroutine { continuation ->
            PremiumDataSource.uploadPremium(months) { result ->
                continuation.resume(result.getOrDefault(false))
            }
        }
    }
}
