package com.drs.auralife.presentation

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterButton
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.content.edit
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.drs.auralife.R
import com.drs.auralife.core.utils.MyAppGlideModule
import com.drs.auralife.core.utils.Notification
import com.drs.auralife.core.utils.PermissionPhotoHandler
import com.drs.auralife.core.utils.UpdateLibraryWorker
import com.drs.auralife.presentation.auth.LoginActivity
import com.drs.auralife.presentation.auth.AuthViewModel
import com.drs.auralife.presentation.common.NotificationAdapter
import com.drs.auralife.presentation.search.SearchController
import com.drs.auralife.presentation.explore.ExploreFragment
import com.drs.auralife.presentation.filmdetails.FilmDetailsActivity
import com.drs.auralife.presentation.filmdetails.EXTRA_SLUG
import com.drs.auralife.presentation.history.HistoryFragment
import com.drs.auralife.presentation.home.HomeFragment
import com.drs.auralife.presentation.library.LibraryFragment
import com.drs.auralife.presentation.payment.PaymentActivity
import com.drs.auralife.presentation.start.ViewPagerAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@dagger.hilt.android.AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val PREF_NAME = "PREFERENCE"
    }
    private val viewPager: ViewPager2 by lazy { findViewById(R.id.view_pager) }
    private val drawerLayout: DrawerLayout by lazy { findViewById(R.id.main_layout) }
    private val navigationView: NavigationView by lazy { findViewById(R.id.navigation_view) }
    private val bottomNavigationView: BottomNavigationView by lazy { findViewById(R.id.bottom_navigation_view) }
    private var permissionPhotoHandler: PermissionPhotoHandler? = null
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var searchController: SearchController? = null

    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupDrawer()
        setupBackPressed()
        setupViewPager()
        initSearchController()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionPhotoHandler?.handlePermissionsResult(requestCode, grantResults)
    }

    private fun setupDrawer() {
        setupDrawerToggle()
        setupDrawerHeader()
        handleDrawerItemSelection()
    }

    private fun setupDrawerToggle() {
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
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
                if (mainViewModel.authState.value) {
                    permissionPhotoHandler?.checkAndRequestPermissions()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
            }

        val navLogin = navigationView.menu.findItem(R.id.navLogin)
        val navLogout = navigationView.menu.findItem(R.id.navLogout)
        val navigationHeader = navigationView.getHeaderView(0)
        val navEmail = navigationHeader.findViewById<TextView>(R.id.navEmail)
        val navPic = navigationHeader.findViewById<ImageView>(R.id.navProfilePic)
        val navPremiumStatus = navigationHeader.findViewById<TextView>(R.id.navPremiumStatus)
        val sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)

        observePremiumStatus(navPremiumStatus, sharedPreferences)
        observeAvatarResult()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.authState.collect { isLoggedIn ->
                    if (isLoggedIn) {
                        navLogin.isVisible = false
                        navLogout.isVisible = true
                        navEmail.text = mainViewModel.userEmail
                        mainViewModel.loadAvatar()
                        navPremiumStatus.visibility = View.VISIBLE
                        mainViewModel.loadPremiumStatus()

                        WorkManager.getInstance(this@MainActivity).enqueueUniquePeriodicWork(
                            "UpdateEpisodeWork",
                            ExistingPeriodicWorkPolicy.KEEP,
                            PeriodicWorkRequestBuilder<UpdateLibraryWorker>(
                                6,
                                TimeUnit.HOURS,
                            ).build(),
                        )

                        WorkManager
                            .getInstance(this@MainActivity)
                            .enqueue(OneTimeWorkRequestBuilder<UpdateLibraryWorker>().build())
                    } else {
                        navLogin.isVisible = true
                        navLogout.isVisible = false
                        navPremiumStatus.visibility = View.GONE
                        navEmail.text = getString(R.string.example_email)
                        navPic.setImageResource(R.drawable.ic_profile)
                        sharedPreferences.edit { putString("ExpireDate", "") }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.avatarState.collect { bitmap ->
                    if (bitmap != null) {
                        MyAppGlideModule.loadImage(context = this@MainActivity, image = bitmap, imageView = navPic)
                    }
                }
            }
        }
    }

    private fun observePremiumStatus(
        navPremiumStatus: TextView,
        sharedPreferences: android.content.SharedPreferences,
    ) {
        lifecycleScope.launch {
            mainViewModel.premiumStatus.collect { status ->
                if (status == null) return@collect
                sharedPreferences.edit { putString("ExpireDate", status.expiryTimestamp?.toString() ?: "") }
                navPremiumStatus.text = if (status.isPremium) getString(R.string.premium) else getString(R.string.freemium)
                navPremiumStatus.setOnClickListener {
                    startActivity(Intent(this@MainActivity, PaymentActivity::class.java))
                }
            }
        }
    }

    private fun handleDrawerItemSelection() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navLogin -> startActivity(Intent(this, LoginActivity::class.java))
                R.id.navLogout -> {
                    authViewModel.logout()
                    Notification.removeAllNotification(this)
                }

                R.id.navExit -> finish()
            }
            true
        }
    }

    private fun uploadAvatar(uri: Uri) {
        contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            mainViewModel.uploadAvatar(bitmap)
        }
    }

    private fun observeAvatarResult() {
        lifecycleScope.launch {
            mainViewModel.avatarResult.collect { result ->
                result.onSuccess {
                    Toast
                        .makeText(
                            this@MainActivity,
                            getString(R.string.upload_avatar_successfully),
                            Toast.LENGTH_SHORT,
                        ).show()
                }.onFailure {
                    Toast
                        .makeText(
                            this@MainActivity,
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
                if (searchController?.handleBackPress() == true) {
                    return
                }
                finish()
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

    private fun initSearchController() {
        val searchBar = findViewById<EditText>(R.id.search_bar)
        val searchLayout = findViewById<LinearLayout>(R.id.search_layout)
        val searchResults = findViewById<RecyclerView>(R.id.search_results)
        searchController = SearchController(
            activity = this,
            searchBar = searchBar,
            searchLayout = searchLayout,
            searchResults = searchResults,
            contentContainer = viewPager,
            bottomNav = bottomNavigationView,
        )
        searchController?.setup()
    }

    override fun onDestroy() {
        searchController?.destroy()
        super.onDestroy()
    }

    @SuppressLint("InflateParams")
    fun setupAppBar(view: FrameLayout) {
        view.addView(layoutInflater.inflate(R.layout.app_bar, null), 0)
        val appBarProfile = view.findViewById<ImageFilterButton>(R.id.app_bar_profile)
        val appBarSearch = view.findViewById<ImageButton>(R.id.app_bar_search)
        val appBarNotifications = view.findViewById<ImageButton>(R.id.app_bar_notifications)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.avatarState.collect { bitmap ->
                    if (bitmap != null) {
                        MyAppGlideModule.loadImage(
                            this@MainActivity,
                            bitmap,
                            appBarProfile,
                        )
                    } else {
                        appBarProfile.setImageResource(R.drawable.ic_profile)
                    }
                }
            }
        }

        appBarProfile.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        appBarSearch.setOnClickListener {
            searchController?.showSearch()
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

        val adapter = NotificationAdapter(
            notifications,
            {
                val intent = Intent(this, FilmDetailsActivity::class.java)
                intent.putExtra(EXTRA_SLUG, it.first)
                startActivity(intent)
            },
            { Notification.removeNotification(this, it) },
        )

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
