package com.drs.auralife.presentation.explore

import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.model.Film

data class ExploreUiState(
    val categories: List<Category> = emptyList(),
    val filmsByCategory: Map<String, List<Film>> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ExploreUiEffect {
    data class ShowToast(val message: String) : ExploreUiEffect
    data class NavigateToCategory(val slug: String, val name: String) : ExploreUiEffect
    data class NavigateToFilm(val slug: String) : ExploreUiEffect
}
