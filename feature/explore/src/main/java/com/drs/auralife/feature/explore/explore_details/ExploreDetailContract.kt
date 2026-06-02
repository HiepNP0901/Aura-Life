package com.drs.auralife.feature.explore.explore_details

import com.drs.auralife.domain.model.Film

data class ExploreDetailUiState(
    val films: List<Film> = emptyList(),
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ExploreDetailUiEffect {
    data class ShowToast(val message: String) : ExploreDetailUiEffect
    data class NavigateToFilm(val slug: String) : ExploreDetailUiEffect
}
