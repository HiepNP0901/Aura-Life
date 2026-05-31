package com.drs.auralife.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.usecase.SearchFilmsUseCase
import com.drs.auralife.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchFilmsUseCase: SearchFilmsUseCase,
) : ViewModel() {

    private val _searchResultsState = MutableStateFlow<UiState<List<Film>>>(UiState.Success(emptyList()))
    val searchResultsState: StateFlow<UiState<List<Film>>> = _searchResultsState.asStateFlow()

    fun searchFilms(keyword: String, limit: Int) {
        viewModelScope.launch {
            _searchResultsState.value = UiState.Loading
            try {
                val films = searchFilmsUseCase(keyword, limit)
                _searchResultsState.value = UiState.Success(films)
            } catch (e: Exception) {
                _searchResultsState.value = UiState.Error(e.message ?: "Search failed")
            }
        }
    }
}
