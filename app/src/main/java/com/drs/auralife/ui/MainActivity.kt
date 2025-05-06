package com.drs.auralife.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterButton
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.edit
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.drs.auralife.R
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.firebase.Authentication
import com.drs.auralife.data.firebase.realtime.database.user.AvatarRepository
import com.drs.auralife.data.firebase.realtime.database.user.premium.PremiumRepository
import com.drs.auralife.ui.auth.LoginActivity
import com.drs.auralife.ui.explore.ExploreFragment
import com.drs.auralife.ui.film.FilmAdapter
import com.drs.auralife.ui.film.HORIZONTAL
import com.drs.auralife.ui.film.SLUG
import com.drs.auralife.ui.film.details.FilmDetailsActivity
import com.drs.auralife.ui.history.HistoryFragment
import com.drs.auralife.ui.home.HomeFragment
import com.drs.auralife.ui.library.LibraryFragment
import com.drs.auralife.ui.payment.PaymentActivity
import com.drs.auralife.utils.MyAppGlideModule
import com.drs.auralife.utils.Notification
import com.drs.auralife.utils.PermissionPhotoHandler
import com.drs.auralife.utils.UpdateLibraryWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import java.util.concurrent.TimeUnit

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
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
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uploadAvatar(it) }
                }
            }

        permissionPhotoHandler = PermissionPhotoHandler(this, activityResultLauncher)

        navigationView
            .getHeaderView(0)
            .findViewById<ImageFilterView>(R.id.navProfilePic)
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
        val navFreemium = navigationHeader.findViewById<TextView>(R.id.navFreemium)
        val sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE)

        Authentication.isLoggedIn.observe(this) {
            if (it) {
                navLogin.isVisible = false
                navLogout.isVisible = true
                navEmail.text = Authentication.getEmail()
                AvatarRepository.getAvatar { MyAppGlideModule.loadImage(this, it, navPic) }
                navFreemium.visibility = View.VISIBLE

                PremiumRepository.getPremiumStatus {
                    sharedPreferences.edit { putString("ExpireDate", it.expireDate) }

                    if (it.status == true) {
                        navFreemium.text = getString(R.string.premium)
                    } else {
                        navFreemium.text = getString(R.string.freemium)
                    }
                    navFreemium.setOnClickListener {
                        startActivity(Intent(this, PaymentActivity::class.java))
                    }
                }
            } else {
                navLogin.isVisible = true
                navLogout.isVisible = false
                navFreemium.visibility = View.GONE
                navEmail.text = getString(R.string.example_email)
                navPic.setImageResource(R.drawable.ic_profile)
                sharedPreferences.edit { putString("ExpireDate", "") }
            }

            if (Authentication.isLoggedIn()) {
                WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "UpdateEpisodeWork",
                    ExistingPeriodicWorkPolicy.KEEP,
                    PeriodicWorkRequestBuilder<UpdateLibraryWorker>(
                        6,
                        TimeUnit.HOURS,
                    ).build(),
                )

                WorkManager
                    .getInstance(this)
                    .enqueue(OneTimeWorkRequestBuilder<UpdateLibraryWorker>().build())
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
                    Notification.removeAllNotification(this)
                }
                R.id.navExit -> finish()
            }
            true
        }
    }

    private fun uploadAvatar(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            AvatarRepository.uploadAvatar(BitmapFactory.decodeStream(inputStream)) {
                it
                    .onSuccess {
                        Toast
                            .makeText(
                                this,
                                getString(R.string.upload_avatar_successfully),
                                Toast.LENGTH_SHORT,
                            ).show()
                        Authentication.isLoggedIn.postValue(true)
                    }.onFailure {
                        Toast
                            .makeText(
                                this,
                                getString(R.string.upload_avatar_failed),
                                Toast.LENGTH_SHORT,
                            ).show()
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
                } else {
                    finish()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun setupViewPager() {
        val fragments = listOf(
            HomeFragment(),
            ExploreFragment(),
            LibraryFragment(),
            HistoryFragment(),
        )

        viewPager.isUserInputEnabled = false
        viewPager.adapter = ViewPagerAdapter(this, fragments)
        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    bottomNavigationView.menu[position].isChecked = true
                }
            },
        )

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> viewPager.currentItem = 0
                R.id.navExplore -> viewPager.currentItem = 1
                R.id.navLibrary -> viewPager.currentItem = 2
                R.id.navHistory -> viewPager.currentItem = 3
            }
            true
        }
    }

    private fun setupSearchBar() {
        val viewModel = FilmsViewModel(this)

        searchResults.layoutManager = LinearLayoutManager(this)
        searchResults.adapter = filmAdapter

        searchBar.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int,
                ) {
                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int,
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isEmpty()) {
                        filmAdapter.clearItems()
                    } else {
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
                }
            },
        )
    }

    @SuppressLint("InflateParams")
    fun setupAppBar(view: FrameLayout) {
        view.addView(layoutInflater.inflate(R.layout.app_bar, null), 0)
        val appBarProfile = view.findViewById<ImageFilterButton>(R.id.app_bar_profile)
        val appBarSearch = view.findViewById<ImageButton>(R.id.app_bar_search)
        val appBarNotifications = view.findViewById<ImageButton>(R.id.app_bar_notifications)

        Authentication.isLoggedIn.observe(this) {
            if (it) {
                AvatarRepository.getAvatar { bitmapImg ->
                    MyAppGlideModule.loadImage(
                        this,
                        bitmapImg,
                        appBarProfile,
                    )
                }
            } else {
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
            showNotificationList(it)
        }
    }

    @SuppressLint("InflateParams")
    private fun showNotificationList(anchor: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_notification_list, null)
        val rvNotifications = popupView.findViewById<RecyclerView>(R.id.rvNotifications)
        val text = popupView.findViewById<TextView>(R.id.text)

        val notifications = Notification.getNotifications(this)

        if (notifications.isEmpty()) {
            text.visibility = View.VISIBLE
        } else {
            text.visibility = View.GONE
        }

        rvNotifications.layoutManager = LinearLayoutManager(this)

        val adapter = NotificationAdapter(notifications, {
            val intent = Intent(this, FilmDetailsActivity::class.java)
            intent.putExtra(SLUG, it.first)
            startActivity(intent)
        }, { Notification.removeNotification(this, it) })

        rvNotifications.adapter = adapter

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true,
        )
        popupWindow.showAsDropDown(anchor)
    }
}
