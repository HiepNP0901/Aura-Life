package com.drs.auralife.presentation.search

import com.drs.auralife.domain.model.Film

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(val films: List<Film>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}

sealed interface SearchUiEffect {
    data class NavigateToFilmDetails(val slug: String) : SearchUiEffect
}
