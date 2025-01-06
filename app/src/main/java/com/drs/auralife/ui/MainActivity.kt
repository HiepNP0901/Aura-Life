package com.drs.auralife.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.drs.auralife.R
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.RealtimeDB
import com.drs.auralife.ui.auth.LoginActivity
import com.drs.auralife.ui.home.HomeFragment
import com.drs.auralife.utils.MyAppGlideModule
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

        initializeViews()
        setupDrawerMenu()
        setupViewPager()
    }

    override fun onResume() {
        super.onResume()
        updateNavigationHeader()
    }

    @SuppressLint("MissingSuperCall")
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionPhotoHandler.handlePermissionsResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionPhotoHandler.handleActivityResult(requestCode, resultCode, data)
    }

    private fun initializeViews() {
        drawerLayout = findViewById(R.id.main_layout)
        navigationDrawer = findViewById(R.id.navigation_view)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        viewPager = findViewById(R.id.viewPager)
        permissionPhotoHandler = PermissionPhotoHandler(this) { uri ->
            uri?.let { uploadAvatar(it) }
        }
    }

    private fun setupDrawerMenu() {
        setupDrawerToggle()
        setupDrawerHeader()
        handleDrawerItemSelection()
    }

    private fun setupDrawerToggle() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    private fun setupDrawerHeader() {
        navigationDrawer.getHeaderView(0).findViewById<ImageFilterView>(R.id.navProfilePic).setOnClickListener {
            if (Authentication.isLoggedIn()) {
                permissionPhotoHandler.checkAndRequestPermissions()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    private fun handleDrawerItemSelection() {
        navigationDrawer.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navLogin -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.navLogout -> handleLogout()
                R.id.navExit -> finish()
            }
            true
        }
    }

    private fun handleLogout() {
        Authentication.logout()
        recreate()
    }

    private fun updateNavigationHeader() {
        val navLogin = navigationDrawer.menu.findItem(R.id.navLogin)
        val navLogout = navigationDrawer.menu.findItem(R.id.navLogout)

        if (Authentication.isLoggedIn()) {
            navLogin.isVisible = false
            navLogout.isVisible = true
            val navigationHeader = navigationDrawer.getHeaderView(0)
            val navEmail = navigationHeader.findViewById<TextView>(R.id.navEmail)
            navEmail.text = Authentication.getEmail()
            val navPic = navigationHeader.findViewById<ImageView>(R.id.navProfilePic)
            RealtimeDB.getAvatar {
                MyAppGlideModule.loadImage(this, it, navPic)
            }
        } else {
            navLogin.isVisible = true
            navLogout.isVisible = false
        }
    }

    private fun uploadAvatar(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            RealtimeDB.uploadAvatar(BitmapFactory.decodeStream(inputStream)) {
                it.onSuccess {
                    Toast.makeText(this, getString(R.string.upload_avatar_successfully), Toast.LENGTH_SHORT).show()
                }.onFailure {
                    Toast.makeText(this, getString(R.string.upload_avatar_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViewPager() {
        val fragments = listOf(HomeFragment())
        viewPager.adapter = ViewPagerAdapter(this, fragments)
        viewPager.isUserInputEnabled = false
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> viewPager.currentItem = 0
                R.id.navSearch -> viewPager.currentItem = 1
                R.id.navLibrary -> viewPager.currentItem = 2
            }
            true
        }
    }
}