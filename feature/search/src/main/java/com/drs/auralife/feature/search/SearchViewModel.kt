package com.drs.auralife.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.result.errorMessage
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
            when (val result = searchFilmsUseCase(keyword, limit)) {
                is Result.Success -> _state.value = SearchUiState.Success(result.data)
                is Result.Error -> _state.value = SearchUiState.Error(result.errorMessage ?: "Search failed")
                is Result.Loading -> {}
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

