package com.drs.auralife.presentation.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drs.auralife.presentation.common.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashFragment : Fragment() {

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeEffect()
        splashViewModel.start()
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                splashViewModel.effect.collect { effect ->
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
