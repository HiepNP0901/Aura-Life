package com.drs.auralife.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.drs.auralife.R
import com.drs.auralife.ui.auth.LoginActivity
import com.drs.auralife.ui.home.HomeActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlin.properties.Delegates

//Splash Screen sẽ hiển thị trong 3s
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var isFirstTime by Delegates.notNull<Boolean>()
    private var auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        isFirstTime =
            getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstTime", true)

        val nextActivity =
            if (isFirstTime) OnboardingActivity::class.java else LoginActivity::class.java
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (auth.currentUser != null) {
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    startActivity(Intent(this, nextActivity))
                }
                finish()
            }, 3000
        )
    }
}