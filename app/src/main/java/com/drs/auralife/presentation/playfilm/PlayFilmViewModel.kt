package com.drs.auralife.presentation.playfilm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.repository.AuthRepository
import com.drs.auralife.domain.usecase.GetPremiumStatusUseCase
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
class PlayFilmViewModel @Inject constructor(
    private val getPremiumStatusUseCase: GetPremiumStatusUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    fun isLoggedIn() = authRepository.isLoggedIn()

    private val _effect = MutableSharedFlow<PlayFilmUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<PlayFilmUiEffect> = _effect.asSharedFlow()

    private val _isPremium = MutableStateFlow(true)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    fun loadPremiumStatus() {
        viewModelScope.launch {
            try {
                val status = getPremiumStatusUseCase()
                _isPremium.value = status.isPremium
            } catch (_: Exception) {
                _isPremium.value = true
            }
        }
    }

    fun checkPlaybackThrottle(position: Long, maxPreviewDurationMs: Long) {
        if (!_isPremium.value && position >= maxPreviewDurationMs) {
            _effect.tryEmit(PlayFilmUiEffect.ShowPremiumDialog)
        }
    }

    fun onUpgradeClicked() {
        viewModelScope.launch {
            if (isLoggedIn()) {
                _effect.emit(PlayFilmUiEffect.NavigateToPayment("Upgrade now"))
            } else {
                _effect.emit(PlayFilmUiEffect.NavigateToLogin)
            }
        }
    }
}
