package com.drs.auralife.presentation.play_film

sealed interface PlayFilmUiEffect {
    data object ShowPremiumDialog : PlayFilmUiEffect
    data class NavigateToPayment(val message: String) : PlayFilmUiEffect
    data object NavigateToLogin : PlayFilmUiEffect
}
