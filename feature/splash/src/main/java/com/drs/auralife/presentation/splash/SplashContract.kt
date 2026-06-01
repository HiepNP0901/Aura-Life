package com.drs.auralife.presentation.splash

sealed interface SplashUiEffect {
    data object NavigateToOnboarding : SplashUiEffect
    data object NavigateToHome : SplashUiEffect
}
