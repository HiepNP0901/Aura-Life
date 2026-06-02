package com.drs.auralife.feature.onboarding

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {

    private val _effect = MutableSharedFlow<OnboardingUiEffect>()
    val effect: SharedFlow<OnboardingUiEffect> = _effect.asSharedFlow()

    fun finishOnboarding() {
        viewModelScope.launch {
            application.getSharedPreferences("PREFERENCE", 0).edit { putBoolean("isFirstTime", false) }
            _effect.emit(OnboardingUiEffect.NavigateToMain)
        }
    }
}
