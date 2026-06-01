package com.drs.auralife.presentation.film_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
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
class FilmDetailsViewModel @Inject constructor(
    private val getFilmDetailsUseCase: GetFilmDetailsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(FilmDetailsUiState())
    val state: StateFlow<FilmDetailsUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FilmDetailsUiEffect>()
    val effect: SharedFlow<FilmDetailsUiEffect> = _effect.asSharedFlow()

    fun getFilmDetails(slug: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val details = getFilmDetailsUseCase(slug)
                _state.value = _state.value.copy(film = details, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
                _effect.emit(FilmDetailsUiEffect.ShowToast(e.message ?: "Unknown error"))
            }
        }
    }

    fun onPlayClicked(slug: String) {
        viewModelScope.launch {
            _effect.emit(FilmDetailsUiEffect.NavigateToPlayFilm(slug))
        }
    }

    fun onLoginNeeded() {
        viewModelScope.launch {
            _effect.emit(FilmDetailsUiEffect.NavigateToLogin)
        }
    }
}
