package com.drs.auralife.presentation.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.R
import com.drs.auralife.presentation.MainActivity
import kotlin.properties.Delegates
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var isFirstTime by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        isFirstTime =
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isFirstTime", true)

        lifecycleScope.launch {
            if (isFirstTime) {
                delay(3000)
                startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
            } else {
                delay(1000)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish()
        }
    }
}


