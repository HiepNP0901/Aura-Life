package com.drs.auralife.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.drs.auralife.databinding.ActivityHomeBinding
import com.drs.auralife.service.AuthService

class HomeActivity : AppCompatActivity() {
    val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private val filter = IntentFilter(AuthService.RESULT)
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getStringExtra("nextActivity")?.let {
                startActivity(Intent(this@HomeActivity, Class.forName(it)))
                finishAffinity()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        binding.logout.setOnClickListener {
            startService(Intent(this, AuthService::class.java).apply {
                action = AuthService.ACTION_LOGOUT
            })
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }
}