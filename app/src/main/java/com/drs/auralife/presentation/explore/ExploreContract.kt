package com.drs.auralife.presentation.explore

import com.drs.auralife.domain.model.Category

data class ExploreUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface ExploreUiEffect {
    data class ShowToast(val message: String) : ExploreUiEffect
    data class NavigateToCategory(val slug: String, val name: String) : ExploreUiEffect
    data class NavigateToFilm(val slug: String) : ExploreUiEffect
}
