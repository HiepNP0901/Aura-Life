package com.drs.auralife.feature.onboarding

sealed interface OnboardingUiEffect {
    data object NavigateToMain : OnboardingUiEffect
}
