package com.drs.auralife.feature.library.library

import com.drs.auralife.domain.model.Library

data class LibraryUiState(
    val libraries: List<Library> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface LibraryUiEffect {
    data class ShowToast(val message: String) : LibraryUiEffect
    data class NavigateToDetails(val name: String) : LibraryUiEffect
}
