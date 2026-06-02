package com.drs.auralife.feature.splash

sealed interface SplashUiEffect {
    data object NavigateToOnboarding : SplashUiEffect
    data object NavigateToHome : SplashUiEffect
}
