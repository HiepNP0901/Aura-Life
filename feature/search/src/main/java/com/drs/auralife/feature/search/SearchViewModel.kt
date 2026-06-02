package com.drs.auralife.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.usecase.SearchFilmsUseCase
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
class SearchViewModel @Inject constructor(
    private val searchFilmsUseCase: SearchFilmsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SearchUiEffect>(extraBufferCapacity = 1)
    val effect: SharedFlow<SearchUiEffect> = _effect.asSharedFlow()

    fun searchFilms(keyword: String, limit: Int) {
        if (keyword.isBlank()) {
            _state.value = SearchUiState.Idle
            return
        }
        viewModelScope.launch {
            _state.value = SearchUiState.Loading
            try {
                val films = searchFilmsUseCase(keyword, limit)
                _state.value = SearchUiState.Success(films)
            } catch (e: Exception) {
                _state.value = SearchUiState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun clearResults() {
        _state.value = SearchUiState.Idle
    }

    fun onFilmClicked(slug: String) {
        _effect.tryEmit(SearchUiEffect.NavigateToFilmDetails(slug))
    }
}
