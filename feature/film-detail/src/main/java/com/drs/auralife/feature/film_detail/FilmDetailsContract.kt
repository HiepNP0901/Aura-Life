package com.drs.auralife.feature.film_detail

import com.drs.auralife.domain.model.FilmDetails

data class FilmDetailsUiState(
    val film: FilmDetails? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

sealed interface FilmDetailsUiEffect {
    data class ShowToast(val message: String) : FilmDetailsUiEffect
    data class NavigateToPlayFilm(val slug: String) : FilmDetailsUiEffect
    data object NavigateToLogin : FilmDetailsUiEffect
}
