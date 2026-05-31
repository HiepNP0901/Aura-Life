package com.drs.auralife.presentation.onboarding

sealed interface OnboardingUiEffect {
    data object NavigateToMain : OnboardingUiEffect
}
