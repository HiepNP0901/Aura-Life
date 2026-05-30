package com.drs.auralife.presentation.playfilm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    private val _throttleEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val throttleEvent: SharedFlow<Unit> = _throttleEvent.asSharedFlow()

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
            _throttleEvent.tryEmit(Unit)
        }
    }
}
