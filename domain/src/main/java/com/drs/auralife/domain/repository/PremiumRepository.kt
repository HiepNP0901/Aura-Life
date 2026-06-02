package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.PremiumStatus
import com.drs.auralife.domain.result.Result

interface PremiumRepository {
    suspend fun getPremiumStatus(): Result<PremiumStatus>
    suspend fun setPremium(months: Int): Boolean
}
