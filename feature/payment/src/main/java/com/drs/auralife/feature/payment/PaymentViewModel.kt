package com.drs.auralife.feature.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.usecase.GetPremiumStatusUseCase
import com.drs.auralife.domain.usecase.SetPremiumUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getPremiumStatusUseCase: GetPremiumStatusUseCase,
    private val setPremiumUseCase: SetPremiumUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentUiState())
    val state: StateFlow<PaymentUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PaymentUiEffect>()
    val effect: SharedFlow<PaymentUiEffect> = _effect.asSharedFlow()

    fun loadPremiumStatus() {
        viewModelScope.launch {
            try {
                val status = getPremiumStatusUseCase()
                _state.value = _state.value.copy(premiumStatus = status)
            } catch (e: Exception) {
                Log.e("PremiumViewModel", "loadPremiumStatus failed", e)
            }
        }
    }

    fun purchasePremium(months: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isPurchasing = true)
            try {
                val result = setPremiumUseCase(months)
                if (result) {
                    loadPremiumStatus()
                    _effect.emit(PaymentUiEffect.PurchaseSuccess("Purchase successful"))
                } else {
                    _effect.emit(PaymentUiEffect.PurchaseError("Payment unavailable"))
                }
            } catch (e: Exception) {
                _effect.emit(PaymentUiEffect.PurchaseError(e.message ?: "Payment unavailable"))
            } finally {
                _state.value = _state.value.copy(isPurchasing = false)
            }
        }
    }
}
