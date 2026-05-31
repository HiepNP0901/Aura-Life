package com.drs.auralife.presentation.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashFragment : Fragment() {

    private val _effect = MutableSharedFlow<SplashUiEffect>()
    private val effect: SharedFlow<SplashUiEffect> = _effect.asSharedFlow()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEffect()
        startSplash()
    }

    private fun startSplash() {
        viewLifecycleOwner.lifecycleScope.launch {
            val isFirstTime = requireActivity()
                .getSharedPreferences("PREFERENCE", 0)
                .getBoolean("isFirstTime", true)

            val delayMs = if (isFirstTime) 3000L else 1000L
            delay(delayMs)
            _effect.emit(
                if (isFirstTime) SplashUiEffect.NavigateToOnboarding
                else SplashUiEffect.NavigateToHome
            )
        }
    }

    private fun observeEffect() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                effect.collect { effect ->
                    when (effect) {
                        is SplashUiEffect.NavigateToOnboarding -> {
                            findNavController().navigate(R.id.action_splash_to_onboarding)
                        }
                        is SplashUiEffect.NavigateToHome -> {
                            findNavController().navigate(R.id.action_splash_to_home)
                        }
                    }
                }
            }
        }
    }
}
