package com.drs.auralife.ui.start

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.drs.auralife.R
import com.drs.auralife.data.model.OnboardingItem
import com.drs.auralife.ui.home.HomeActivity
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var onboardingViewPager: ViewPager2
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_onboarding)
        btnNext = findViewById<Button>(R.id.onboardingButtonNext)
        onboardingViewPager = findViewById(R.id.onboardingViewPager)
        setupOnboardingItems()

        TabLayoutMediator(findViewById(R.id.tabLayout), onboardingViewPager) { tab, position ->
        }.attach()

        btnNext.setOnClickListener {
            onboardingViewPager.currentItem += 1
        }

        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onboardingAdapter.itemCount - 1) {
                    btnNext.text = getString(R.string.start)
                    btnNext.setOnClickListener {
                        val sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
                        startActivity(Intent(this@OnboardingActivity, HomeActivity::class.java))
                        finish()
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
            OnboardingItem(
                R.drawable.bg_onboarding_1,
                getString(R.string.onboarding_description_1)
            ),
            OnboardingItem(
                R.drawable.bg_onboarding_2,
                getString(R.string.onboarding_description_2)
            ),
            OnboardingItem(
                R.drawable.bg_onboarding_3,
                getString(R.string.onboarding_description_3)
            )
        )

        onboardingAdapter = OnboardingAdapter(onboardingItems)
        onboardingViewPager.adapter = onboardingAdapter
        onboardingViewPager.currentItem = 0
    }
}