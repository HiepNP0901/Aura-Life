package com.drs.auralife.data.repository

import com.drs.auralife.domain.result.Result
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
    override suspend fun getPremiumStatus(): Result<PremiumStatus> {
        return try {
            val status = suspendCancellableCoroutine { continuation ->
                premiumDataSource.getPremiumStatus { firebasePremium ->
                    continuation.resume(firebasePremium.toDomainPremiumStatus())
                }
            }
            Result.Success(status)
        } catch (e: Exception) {
            Result.Error(e)
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
