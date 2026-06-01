package com.drs.auralife.presentation.splash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val application: Application,
) : ViewModel() {

    private val _effect = MutableSharedFlow<SplashUiEffect>()
    val effect: SharedFlow<SplashUiEffect> = _effect.asSharedFlow()

    fun start() {
        viewModelScope.launch {
            val prefs = application.getSharedPreferences("PREFERENCE", 0)
            val isFirstTime = prefs.getBoolean("isFirstTime", true)
            val delayMs = if (isFirstTime) 3000L else 1000L
            delay(delayMs)
            _effect.emit(
                if (isFirstTime) SplashUiEffect.NavigateToOnboarding
                else SplashUiEffect.NavigateToHome
            )
        }
    }
}
