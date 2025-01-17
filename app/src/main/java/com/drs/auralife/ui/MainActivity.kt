package com.drs.auralife.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterButton
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.drs.auralife.R
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.RealtimeDB
import com.drs.auralife.ui.auth.LoginActivity
import com.drs.auralife.ui.explore.ExploreFragment
import com.drs.auralife.ui.film.FilmAdapter
import com.drs.auralife.ui.film.HORIZONTAL
import com.drs.auralife.ui.home.HomeFragment
import com.drs.auralife.ui.library.LibraryFragment
import com.drs.auralife.utils.MyAppGlideModule
import com.drs.auralife.utils.PermissionPhotoHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var searchBar: EditText
    private lateinit var searchLayout: LinearLayout
    private lateinit var searchResults: RecyclerView
    private lateinit var viewPager: ViewPager2
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var permissionPhotoHandler: PermissionPhotoHandler
    private var searchIsVisible = false
    private val filmAdapter = FilmAdapter(mutableListOf(), HORIZONTAL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupDrawer()
        setupBackPressed()
        setupViewPager()
        setupSearchBar()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionPhotoHandler.handlePermissionsResult(requestCode, grantResults)
    }

    private fun initializeViews() {
        viewPager = findViewById(R.id.view_pager)
        drawerLayout = findViewById(R.id.main_layout)
        searchLayout = findViewById(R.id.search_layout)
        searchBar = findViewById(R.id.search_bar)
        searchResults = findViewById(R.id.search_results)
        navigationView = findViewById(R.id.navigation_view)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
    }


    private fun setupDrawer() {
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
        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uploadAvatar(it) }
            }
        }

        permissionPhotoHandler = PermissionPhotoHandler(this, activityResultLauncher)

        navigationView.getHeaderView(0).findViewById<ImageFilterView>(R.id.navProfilePic)
            .setOnClickListener {
            if (Authentication.isLoggedIn()) {
                permissionPhotoHandler.checkAndRequestPermissions()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        val navLogin = navigationView.menu.findItem(R.id.navLogin)
        val navLogout = navigationView.menu.findItem(R.id.navLogout)
        val navigationHeader = navigationView.getHeaderView(0)
        val navEmail = navigationHeader.findViewById<TextView>(R.id.navEmail)
        val navPic = navigationHeader.findViewById<ImageView>(R.id.navProfilePic)

        Authentication.isLoggedIn.observe(this) {
            if (it) {
                navLogin.isVisible = false
                navLogout.isVisible = true
                navEmail.text = Authentication.getEmail()
                RealtimeDB.getAvatar { MyAppGlideModule.loadImage(this, it, navPic) }
            }
            else {
                navLogin.isVisible = true
                navLogout.isVisible = false
                navEmail.text = getString(R.string.example_email)
                navPic.setImageResource(R.drawable.ic_profile)
            }
        }
    }


    private fun handleDrawerItemSelection() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navLogin -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.navLogout -> {
                    Authentication.logout()
                    Authentication.isLoggedIn.postValue(false)
                }
                R.id.navExit -> finish()
            }
            true
        }
    }


    private fun uploadAvatar(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            RealtimeDB.uploadAvatar(BitmapFactory.decodeStream(inputStream)) {
                it.onSuccess {
                    Toast.makeText(this, getString(R.string.upload_avatar_successfully), Toast.LENGTH_SHORT).show()
                    Authentication.isLoggedIn.postValue(true)
                }.onFailure {
                    Toast.makeText(this, getString(R.string.upload_avatar_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (searchIsVisible) {
                    searchLayout.visibility = View.GONE
                    bottomNavigationView.visibility = View.VISIBLE
                    viewPager.visibility = View.VISIBLE
                    searchIsVisible = false
                    filmAdapter.clearItems()
                    searchBar.text.clear()
                }
                else {
                    finish()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }


    private fun setupViewPager() {
        val fragments = listOf(
            HomeFragment(), ExploreFragment(),
            LibraryFragment()
        )

        viewPager.isUserInputEnabled = false
        viewPager.adapter = ViewPagerAdapter(this, fragments)
        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    bottomNavigationView.menu.getItem(position).isChecked = true
                }
            }
        )

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> viewPager.currentItem = 0
                R.id.navExplore -> viewPager.currentItem = 1
                R.id.navLibrary -> viewPager.currentItem = 2
            }
            true
        }
    }

    private fun setupSearchBar() {
        val viewModel = ViewModelProvider(this, FilmViewModelFactory(this))[FilmsViewModel::class.java]

        searchResults.layoutManager = LinearLayoutManager(this)
        searchResults.adapter = filmAdapter

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.searchFilms(s.toString(), 5) {
                    it?.data?.let {
                        for (item in it.items) {
                            item.posterUrl = it.appDomainCdnImage + "/" + item.posterUrl
                            item.thumbUrl = it.appDomainCdnImage + "/" + item.thumbUrl
                        }
                        filmAdapter.replaceItems(it.items)
                    }
                }
            }
        })
    }

    @SuppressLint("InflateParams")
    fun setupAppBar(view: FrameLayout) {
        view.addView(layoutInflater.inflate(R.layout.app_bar, null), 0)
        val appBarProfile = view.findViewById<ImageFilterButton>(R.id.app_bar_profile)
        val appBarSearch = view.findViewById<ImageButton>(R.id.app_bar_search)
        val appBarNotifications = view.findViewById<ImageButton>(R.id.app_bar_notifications)

        Authentication.isLoggedIn.observe(this) {
            if (it) {
                RealtimeDB.getAvatar { bitmapImg ->
                    MyAppGlideModule.loadImage(
                        this, bitmapImg, appBarProfile
                    )
                }
            }
            else {
                appBarProfile.setImageResource(R.drawable.ic_profile)
            }
        }

        appBarProfile.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        appBarSearch.setOnClickListener {
            searchLayout.visibility = View.VISIBLE
            bottomNavigationView.visibility = View.GONE
            viewPager.visibility = View.GONE
            searchBar.requestFocus()
            searchIsVisible = true
        }

        appBarNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        }
    }
}