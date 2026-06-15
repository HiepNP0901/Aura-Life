package com.drs.auralife.feature.film_player

sealed interface PlayFilmUiEffect {
    data object ShowPremiumDialog : PlayFilmUiEffect
    data class NavigateToPayment(val message: String) : PlayFilmUiEffect
    data object NavigateToLogin : PlayFilmUiEffect
}
