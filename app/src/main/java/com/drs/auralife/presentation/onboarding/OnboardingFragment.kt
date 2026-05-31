package com.drs.auralife.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.drs.auralife.R
import com.drs.auralife.domain.model.OnboardingItem
import com.drs.auralife.presentation.onboarding.adapter.OnboardingAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var onboardingViewPager: ViewPager2
    private lateinit var btnNext: Button

    private val _effect = MutableSharedFlow<OnboardingUiEffect>()
    private val effect: SharedFlow<OnboardingUiEffect> = _effect.asSharedFlow()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_onboarding, container, false)
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
                        requireActivity().getSharedPreferences("PREFERENCE", 0).edit { putBoolean("isFirstTime", false) }
                        findNavController().navigate(R.id.action_onboarding_to_home)
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                effect.collect { effect ->
                    when (effect) {
                        is OnboardingUiEffect.NavigateToMain -> {
                            requireActivity().getSharedPreferences("PREFERENCE", 0).edit { putBoolean("isFirstTime", false) }
                            findNavController().navigate(R.id.action_onboarding_to_home)
                        }
                    }
                }
            }
        }
    }
}
