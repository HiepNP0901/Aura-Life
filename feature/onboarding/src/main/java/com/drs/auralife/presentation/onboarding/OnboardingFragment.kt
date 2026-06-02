package com.drs.auralife.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.drs.auralife.feature.onboarding.R
import com.drs.auralife.domain.model.OnboardingItem
import com.drs.auralife.navigation.NavRoutes
import com.drs.auralife.presentation.onboarding.adapter.OnboardingAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private val onboardingViewModel: OnboardingViewModel by viewModels()
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var onboardingViewPager: ViewPager2
    private lateinit var btnNext: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnNext = view.findViewById(R.id.onboardingButtonNext)
        onboardingViewPager = view.findViewById(R.id.onboardingViewPager)
        setupOnboardingItems()

        TabLayoutMediator(view.findViewById(R.id.tabLayout), onboardingViewPager) { _, _ -> }.attach()

        observeEffect()

        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onboardingAdapter.itemCount - 1) {
                    btnNext.text = getString(R.string.start)
                    btnNext.setOnClickListener {
                        onboardingViewModel.finishOnboarding()
                    }
                } else {
                    btnNext.text = getString(R.string.next)
                    btnNext.setOnClickListener {
                        onboardingViewPager.currentItem += 1
                    }
                }
            }
        })
    }

    private fun setupOnboardingItems() {
        val onboardingItems = listOf(
            OnboardingItem(R.drawable.bg_onboarding_1, getString(R.string.onboarding_description_1)),
            OnboardingItem(R.drawable.bg_onboarding_2, getString(R.string.onboarding_description_2)),
            OnboardingItem(R.drawable.bg_onboarding_3, getString(R.string.onboarding_description_3)),
        )
        onboardingAdapter = OnboardingAdapter(onboardingItems)
        onboardingViewPager.adapter = onboardingAdapter
        onboardingViewPager.currentItem = 0
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                onboardingViewModel.effect.collect { effect ->
                    when (effect) {
                        is OnboardingUiEffect.NavigateToMain -> {
                            findNavController().navigate(
                                NavRoutes.HOME,
                                NavOptions.Builder().setPopUpTo(R.id.onboarding, true).build(),
                            )
                        }
                    }
                }
        }
    }
}
