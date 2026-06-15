package com.drs.auralife.feature.film_player

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.repository.AuthRepository
import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.usecase.GetPremiumStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmPlayerViewModel @Inject constructor(
    private val getPremiumStatusUseCase: GetPremiumStatusUseCase,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val slug: String = savedStateHandle.get<String>("slug") ?: ""

    private val _state = MutableStateFlow(FilmPlayerUiState(slug = slug))
    val state: StateFlow<FilmPlayerUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PlayFilmUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<PlayFilmUiEffect> = _effect.asSharedFlow()

    private val _isPremium = MutableStateFlow(true)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    fun setCurrentEpisode(episode: Int) {
        _state.update { it.copy(currentEpisode = episode) }
    }

    fun setCurrentPosition(position: Long) {
        _state.update { it.copy(currentPosition = position) }
    }

    fun toggleFullscreen() {
        _state.update { it.copy(isFullscreen = !it.isFullscreen) }
    }

    fun restoreState(episode: Int, position: Long, fullscreen: Boolean) {
        _state.update { it.copy(currentEpisode = episode, currentPosition = position, isFullscreen = fullscreen) }
    }

    fun loadPremiumStatus() {
        viewModelScope.launch {
            when (val result = getPremiumStatusUseCase()) {
                is Result.Success -> _isPremium.value = result.data.isPremium
                is Result.Error -> _isPremium.value = true
                is Result.Loading -> {}
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

    private fun isLoggedIn() = authRepository.isLoggedIn()
}
