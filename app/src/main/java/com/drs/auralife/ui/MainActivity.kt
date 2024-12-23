package com.drs.auralife.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.drs.auralife.data.AuthService
import com.drs.auralife.databinding.ActivityMainBinding
import com.drs.auralife.ui.auth.LoginActivity
import com.drs.auralife.R
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open, R.string.close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        // Set up the navigation item selection listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            handleNavigationItemSelected(menuItem)
            true
        }

        SoundViewModel().getAllSounds {
            it.onSuccess {
                Log.d("SoundViewModel", "Success: $it")
                SoundViewModel().getSoundById(it.results[0].id){
                    it.onSuccess {
                        Log.d("SoundViewModel", "Success: $it")
                    }
                    it.onFailure {
                        Log.d("SoundViewModel", "Failure: ${it.message}")
                    }
                }
            }
            it.onFailure {
                Log.d("SoundViewModel", "Failure: ${it.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(AuthService().isLoggedIn()){
            navigationView.menu.findItem(R.id.navLogin).isVisible = false
            navigationView.menu.findItem(R.id.navLogout).isVisible = true
            navigationView.getHeaderView(0).findViewById<TextView>(R.id.navEmail).text = AuthService().getEmail()
            navigationView.getHeaderView(0).findViewById<ImageView>(R.id.navProfilePic).setImageResource(R.drawable.bg_logo)
        }
        else{
            navigationView.menu.findItem(R.id.navLogin).isVisible = true
            navigationView.menu.findItem(R.id.navLogout).isVisible = false
        }
    }

    @SuppressLint("UnsafeIntentLaunch")
    private fun handleNavigationItemSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.navProfile -> {
                // Handle profile item click
            }
            R.id.navSettings -> {
                // Handle settings item click
            }
            R.id.navLogin -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.navLogout -> {
                AuthService().logout()
                finish()
                startActivity(intent)
            }
        }
    }

    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onBackPressed() {
        // Close the drawer if it's open, otherwise handle back press as usual
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}