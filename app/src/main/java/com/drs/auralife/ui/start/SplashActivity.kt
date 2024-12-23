package com.drs.auralife.ui.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.drs.auralife.R
import com.drs.auralife.ui.MainActivity
import kotlin.properties.Delegates

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var isFirstTime by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        isFirstTime =
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstTime", true)
        if (isFirstTime) {
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, OnboardingActivity::class.java))
                finish()
            }, 3000)
        } else{
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 1000)
        }
    }
}