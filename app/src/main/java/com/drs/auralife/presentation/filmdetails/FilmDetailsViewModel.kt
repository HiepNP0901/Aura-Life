package com.drs.auralife.presentation.filmdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
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

    private val _filmDetailsState = MutableStateFlow<FilmDetails?>(null)
    val filmDetailsState: StateFlow<FilmDetails?> = _filmDetailsState.asStateFlow()

    private val _isLoadingState = MutableStateFlow(false)
    val isLoadingState: StateFlow<Boolean> = _isLoadingState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    fun getFilmDetails(slug: String) {
        viewModelScope.launch {
            try {
                _isLoadingState.value = true
                _errorState.value = null
                val details = getFilmDetailsUseCase(slug)
                _filmDetailsState.value = details
            } catch (e: Exception) {
                _errorState.value = e.message
            } finally {
                _isLoadingState.value = false
            }
        }
    }
}
