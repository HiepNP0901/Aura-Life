package com.drs.auralife.feature.payment

import com.drs.auralife.domain.model.PremiumStatus

data class PaymentUiState(
    val premiumStatus: PremiumStatus? = null,
    val isPurchasing: Boolean = false,
)

sealed interface PaymentUiEffect {
    data class ShowToast(val message: String) : PaymentUiEffect
    data class PurchaseSuccess(val message: String) : PaymentUiEffect
    data class PurchaseError(val message: String) : PaymentUiEffect
}
