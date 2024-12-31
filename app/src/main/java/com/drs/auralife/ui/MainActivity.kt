package com.drs.auralife.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.drs.auralife.R
import com.drs.auralife.data.AuthService
import com.drs.auralife.ui.auth.LoginActivity
import com.drs.auralife.ui.film.FilmAdapter
import com.drs.auralife.ui.fragmentPage.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationDrawer: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupDrawerMenu()

        setupViewPager()
    }

    override fun onResume() {
        super.onResume()

        var navLogin = navigationDrawer.menu.findItem(R.id.navLogin)
        var navLogout = navigationDrawer.menu.findItem(R.id.navLogout)

        if (AuthService.isLoggedIn()) {
            navLogin.isVisible = false
            navLogout.isVisible = true

            val navigationHeader = navigationDrawer.getHeaderView(0)

            val navEmail = navigationHeader.findViewById<TextView>(R.id.navEmail)
            navEmail.text = AuthService.getEmail()

            val navPic = navigationHeader.findViewById<ImageView>(R.id.navProfilePic)
            navPic.setImageResource(R.drawable.bg_logo)
        } else {
            navLogin.isVisible = true
            navLogout.isVisible = false
        }
    }

    @SuppressLint("MissingSuperCall")
    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onBackPressed() {

        // Close the drawer if it's open, otherwise handle back press as usual
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    @SuppressLint("UnsafeIntentLaunch")
    private fun setupDrawerMenu() {

        drawerLayout = findViewById(R.id.drawerLayout)

        navigationDrawer = findViewById(R.id.navigation_view)

        // Set up the navigation drawer
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.open, R.string.close
        )

        // Set up the navigation drawer toggle
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()

        // Handle navigation drawer item clicks
        navigationDrawer.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                R.id.navProfile -> {
                    // TODO: Handle profile item click
                }

                R.id.navSettings -> {
                    // TODO: Handle settings item click
                }

                R.id.navLogin -> {
                    startActivity(
                        Intent(
                            this,
                            LoginActivity::class.java
                        )
                    )
                }

                R.id.navLogout -> {
                    AuthService.logout()
                    finish()
                    startActivity(intent)
                }

            }
            true
        }

    }

    private fun setupViewPager() {

        val fragments = listOf(
            HomeFragment(),
            //SearchFragment(),
            //ExploreFragment(),
            //LibraryFragment()
        )

        viewPager = findViewById(R.id.viewPager)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        viewPager.adapter = ViewPagerAdapter(this, fragments)

        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    bottomNavigationView.menu.getItem(position).isChecked = true
                }
            }
        )

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> viewPager.currentItem = 0
                R.id.navSearch -> viewPager.currentItem = 1
                R.id.navExplore -> viewPager.currentItem = 2
                R.id.navLibrary -> viewPager.currentItem = 3
            }
            true
        }
    }
}