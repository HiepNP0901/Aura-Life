package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.PremiumStatus

interface PremiumRepository {
    suspend fun getPremiumStatus(): PremiumStatus
    suspend fun setPremium(months: Int): Boolean
}
