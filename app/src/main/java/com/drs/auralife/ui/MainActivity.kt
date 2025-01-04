package com.drs.auralife.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.drs.auralife.R
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.RealtimeDB
import com.drs.auralife.ui.auth.LoginActivity
import com.drs.auralife.ui.home.HomeFragment
import com.drs.auralife.utils.PermissionPhotoHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationDrawer: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var viewPager: ViewPager2
    private lateinit var permissionPhotoHandler: PermissionPhotoHandler

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

        if (Authentication.isLoggedIn()) {
            navLogin.isVisible = false
            navLogout.isVisible = true

            val navigationHeader = navigationDrawer.getHeaderView(0)

            val navEmail = navigationHeader.findViewById<TextView>(R.id.navEmail)
            navEmail.text = Authentication.getEmail()

            val navPic = navigationHeader.findViewById<ImageView>(R.id.navProfilePic)
            RealtimeDB.getAvatar {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(navPic)
            }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionPhotoHandler.handlePermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionPhotoHandler.handleActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("UnsafeIntentLaunch")
    private fun setupDrawerMenu() {

        drawerLayout = findViewById(R.id.main_layout)
        navigationDrawer = findViewById(R.id.navigation_view)

        // Initialize the PermissionPhotoHandler
        permissionPhotoHandler = PermissionPhotoHandler(this) { uri ->
            uri?.let {
                contentResolver
                    .openInputStream(it)
                    ?.use { inputStream ->
                        RealtimeDB.uploadAvatar(this, BitmapFactory.decodeStream(inputStream))
                    }
            }
        }

        navigationDrawer
            .getHeaderView(0)
            .findViewById<ImageFilterView>(R.id.navProfilePic)
            .setOnClickListener { permissionPhotoHandler.checkAndRequestPermissions() }

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
                R.id.navLogin -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                R.id.navLogout -> {
                    Authentication.logout()
                    finish()
                    startActivity(intent)
                }
                R.id.navExit -> {
                    finish()
                }
            }
            true
        }

    }


    private fun setupViewPager() {

        val fragments = listOf(
            HomeFragment(),
            //SearchFragment(),
            //LibraryFragment()
        )

        viewPager = findViewById(R.id.viewPager)

        viewPager.isUserInputEnabled = false

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
                R.id.navLibrary -> viewPager.currentItem = 2
                //R.id.navExplore -> viewPager.currentItem = 2
                //R.id.navLibrary -> viewPager.currentItem = 3
            }
            true
        }
    }
}