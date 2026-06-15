package com.drs.auralife.feature.history

import com.drs.auralife.feature.history.model.HistoryFilm

data class HistoryUiState(
    val films: List<HistoryFilm> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface HistoryUiEffect {
    data class ShowToast(val message: String) : HistoryUiEffect
    data class NavigateToFilmPlayer(val slug: String) : HistoryUiEffect
}
