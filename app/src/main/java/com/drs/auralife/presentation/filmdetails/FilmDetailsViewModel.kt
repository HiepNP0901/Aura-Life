package com.drs.auralife.presentation.filmdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
import com.drs.auralife.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmDetailsViewModel @Inject constructor(
    private val getFilmDetailsUseCase: GetFilmDetailsUseCase,
) : ViewModel() {

    private val _filmDetailsState = MutableStateFlow<UiState<FilmDetails>>(UiState.Loading)
    val filmDetailsState: StateFlow<UiState<FilmDetails>> = _filmDetailsState.asStateFlow()

    fun getFilmDetails(slug: String) {
        viewModelScope.launch {
            _filmDetailsState.value = UiState.Loading
            try {
                val details = getFilmDetailsUseCase(slug)
                if (details != null) {
                    _filmDetailsState.value = UiState.Success(details)
                } else {
                    _filmDetailsState.value = UiState.Error("Film not found")
                }
            } catch (e: Exception) {
                _filmDetailsState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
