package com.drs.auralife.presentation.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.PremiumStatus
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
class PremiumViewModel @Inject constructor(
    private val getPremiumStatusUseCase: GetPremiumStatusUseCase,
    private val setPremiumUseCase: SetPremiumUseCase,
) : ViewModel() {

    private val _premiumStatus = MutableStateFlow<PremiumStatus?>(null)
    val premiumStatus: StateFlow<PremiumStatus?> = _premiumStatus.asStateFlow()

    private val _purchaseResult = MutableSharedFlow<Result<Boolean>>()
    val purchaseResult: SharedFlow<Result<Boolean>> = _purchaseResult.asSharedFlow()

    fun loadPremiumStatus() {
        viewModelScope.launch {
            try {
                _premiumStatus.value = getPremiumStatusUseCase()
            } catch (e: Exception) {
                Log.e("PremiumViewModel", "loadPremiumStatus failed", e)
            }
        }
    }

    fun purchasePremium(months: Int) {
        viewModelScope.launch {
            try {
                val result = setPremiumUseCase(months)
                _purchaseResult.emit(Result.success(result))
                if (result) loadPremiumStatus()
            } catch (e: Exception) {
                _purchaseResult.emit(Result.failure(e))
            }
        }
    }
}
