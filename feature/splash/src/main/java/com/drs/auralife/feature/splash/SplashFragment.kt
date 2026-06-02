package com.drs.auralife.feature.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.drs.auralife.core.navigation.AppNavigator
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import com.drs.auralife.navigation.NavRoutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashFragment : Fragment() {

    private val appNavigator by lazy { AppNavigator(findNavController()) }

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_splash, container, false)
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
                        appNavigator.navigateTo(NavRoutes.ONBOARDING) { popUpTo(NavRoutes.SPLASH) { inclusive = true } }
                    }

                    is SplashUiEffect.NavigateToHome -> {
                        appNavigator.navigateTo(NavRoutes.HOME) { popUpTo(NavRoutes.SPLASH) { inclusive = true } }
                    }
                }
            }
        }
    }
}
