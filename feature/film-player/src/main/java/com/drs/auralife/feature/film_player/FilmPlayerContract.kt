package com.drs.auralife.feature.film_player

data class FilmPlayerUiState(
    val slug: String = "",
    val currentEpisode: Int = 0,
    val currentPosition: Long = 0,
    val isFullscreen: Boolean = false,
)

sealed interface PlayFilmUiEffect {
    data object ShowPremiumDialog : PlayFilmUiEffect
    data class NavigateToPayment(val message: String) : PlayFilmUiEffect
    data object NavigateToLogin : PlayFilmUiEffect
}
