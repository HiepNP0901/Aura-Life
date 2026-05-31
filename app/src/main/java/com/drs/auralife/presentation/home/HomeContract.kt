package com.drs.auralife.presentation.home

import com.drs.auralife.domain.model.Film

data class HomeUiState(
    val banners: List<Pair<String, String>> = emptyList(),
    val films: List<Film> = emptyList(),
    val totalPages: Int = 0,
    val isLoadingBanners: Boolean = false,
    val isLoadingFilms: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface HomeUiEffect {
    data class ShowToast(val message: String) : HomeUiEffect
    data class NavigateToFilm(val slug: String) : HomeUiEffect
}
