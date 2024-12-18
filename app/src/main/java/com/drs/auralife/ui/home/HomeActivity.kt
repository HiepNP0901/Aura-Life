package com.drs.auralife.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.drs.auralife.data.AuthResponsible
import com.drs.auralife.databinding.ActivityHomeBinding
import com.drs.auralife.ui.auth.LoginActivity

class HomeActivity : AppCompatActivity() {
    val binding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.logout.setOnClickListener {
            AuthResponsible().logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}