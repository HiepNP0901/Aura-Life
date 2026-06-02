package com.drs.auralife.feature.film_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.result.errorMessage
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
            when (val result = getFilmDetailsUseCase(slug)) {
                is Result.Success -> _state.value = _state.value.copy(film = result.data, isLoading = false)
                is Result.Error -> {
                    _state.value = _state.value.copy(isLoading = false, errorMessage = result.errorMessage)
                    _effect.emit(FilmDetailsUiEffect.ShowToast(result.errorMessage ?: "Unknown error"))
                }

                is Result.Loading -> {}
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

