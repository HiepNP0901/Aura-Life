package com.drs.auralife.presentation.history

import com.drs.auralife.domain.model.Film

data class HistoryUiState(
    val films: List<Film> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface HistoryUiEffect {
    data class ShowToast(val message: String) : HistoryUiEffect
    data class NavigateToFilm(val slug: String) : HistoryUiEffect
}
