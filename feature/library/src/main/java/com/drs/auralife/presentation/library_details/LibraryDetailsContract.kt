package com.drs.auralife.presentation.library_details

import com.drs.auralife.domain.model.Film

data class LibraryDetailUiState(
    val films: List<Film> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LibraryDetailUiEffect {
    data class ShowToast(val message: String) : LibraryDetailUiEffect
    data class NavigateToFilm(val slug: String) : LibraryDetailUiEffect
}
