package com.drs.auralife.domain.model

data class PremiumStatus(
    val isPremium: Boolean,
    val expiryTimestamp: Long?,
)
