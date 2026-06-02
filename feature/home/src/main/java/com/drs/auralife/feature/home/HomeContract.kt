package com.drs.auralife.feature.home

import com.drs.auralife.domain.model.Banner
import com.drs.auralife.domain.model.Film

data class HomeUiState(
    val banners: List<Banner> = emptyList(),
    val films: List<Film> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val isLoadingBanners: Boolean = false,
    val isLoadingFilms: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isConnected: Boolean = true,
    val errorMessage: String? = null,
)

sealed interface HomeUiEffect {
    data class ShowToast(val message: String) : HomeUiEffect
    data class NavigateToFilm(val slug: String) : HomeUiEffect
}
