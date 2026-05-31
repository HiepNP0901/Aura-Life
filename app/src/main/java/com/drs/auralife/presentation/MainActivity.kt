package com.drs.auralife.presentation

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.utils.widget.ImageFilterButton
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.edit
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.drs.auralife.R
import com.drs.auralife.core.util.Notification
import com.drs.auralife.core.worker.UpdateLibraryWorker
import com.drs.auralife.presentation.common.MyAppGlideModule
import com.drs.auralife.presentation.common.NotificationPopupHelper
import com.drs.auralife.presentation.common.PermissionPhotoHandler
import com.drs.auralife.presentation.common.launchAndRepeatOnStarted
import com.drs.auralife.presentation.navigation.NavRoutes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppBarProvider {
    companion object {
        private const val PREF_NAME = "PREFERENCE"
    }

    private var navController: NavController? = null
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var bottomNavigationView: BottomNavigationView? = null
    private var permissionPhotoHandler: PermissionPhotoHandler? = null
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        drawerLayout = findViewById(R.id.main_layout)
        navigationView = findViewById(R.id.navigation_view)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        setupBottomNav()
        setupDrawer()
        setupBackPressed()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle?.syncState()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionPhotoHandler?.handlePermissionsResult(requestCode, grantResults)
    }

    private fun setupBottomNav() {
        bottomNavigationView?.setOnItemSelectedListener { item ->
            val route = when (item.itemId) {
                R.id.home -> NavRoutes.HOME
                R.id.explore -> NavRoutes.EXPLORE
                R.id.library -> NavRoutes.LIBRARY
                R.id.history -> NavRoutes.HISTORY
                else -> null
            }
            if (route != null) {
                navController?.navigate(route) {
                    popUpTo(NavRoutes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
                true
            } else {
                false
            }
        }

        val navHeight = (75 * resources.displayMetrics.density).toInt()

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            val show = destination.route in setOf(
                NavRoutes.HOME, NavRoutes.EXPLORE, NavRoutes.LIBRARY, NavRoutes.HISTORY,
            )
            bottomNavigationView?.visibility = if (show) View.VISIBLE else View.GONE
            val navHost = findViewById<View>(R.id.nav_host_fragment)
            (navHost.layoutParams as CoordinatorLayout.LayoutParams)
                .bottomMargin = if (show) navHeight else 0
        }
    }

    private fun setupDrawer() {
        setupDrawerToggle()
        setupNavProfileClick()
        observeAuthState()
        observePremiumStatus()
        observeAvatar()
        observeAvatarResult()
        scheduleLibraryWorker()
        handleDrawerItemSelection()
    }

    private fun setupDrawerToggle() {
        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        actionBarDrawerToggle?.let { drawerLayout?.addDrawerListener(it) }
    }

    private fun setupNavProfileClick() {
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uploadAvatar(it) }
                }
            }

        permissionPhotoHandler = PermissionPhotoHandler(this, activityResultLauncher)

        navigationView
            ?.getHeaderView(0)
            ?.findViewById<ImageFilterView>(R.id.navProfilePic)
            ?.setOnClickListener {
                if (mainViewModel.authState.value) {
                    permissionPhotoHandler?.checkAndRequestPermissions()
                } else {
                    navController?.navigate(NavRoutes.LOGIN)
                }
            }
    }

    private fun observeAuthState() {
        launchAndRepeatOnStarted {
            mainViewModel.authState.collect { isLoggedIn ->
                val navLogin = navigationView?.menu?.findItem(R.id.navLogin)
                val navLogout = navigationView?.menu?.findItem(R.id.navLogout)
                val navigationHeader = navigationView?.getHeaderView(0)
                val navEmail = navigationHeader?.findViewById<TextView>(R.id.navEmail)
                val navPic = navigationHeader?.findViewById<ImageView>(R.id.navProfilePic)
                val navPremiumStatus = navigationHeader?.findViewById<TextView>(R.id.navFreemium)

                if (isLoggedIn) {
                    navLogin?.isVisible = false
                    navLogout?.isVisible = true
                    navEmail?.text = mainViewModel.userEmail
                    mainViewModel.loadAvatar()
                    navPremiumStatus?.visibility = View.VISIBLE
                    mainViewModel.loadPremiumStatus()
                } else {
                    navLogin?.isVisible = true
                    navLogout?.isVisible = false
                    navPremiumStatus?.visibility = View.GONE
                    navEmail?.text = getString(R.string.example_email)
                    navPic?.setImageResource(R.drawable.ic_profile)
                    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                        .edit { putString("ExpireDate", "") }
                }
            }
        }
    }

    private fun observePremiumStatus() {
        launchAndRepeatOnStarted {
            mainViewModel.premiumStatus.collect { status ->
                if (status == null) return@collect
                val navPremiumStatus = navigationView
                    ?.getHeaderView(0)
                    ?.findViewById<TextView>(R.id.navFreemium)
                getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    .edit { putString("ExpireDate", status.expiryTimestamp?.toString() ?: "") }
                navPremiumStatus?.text =
                    if (status.isPremium) getString(R.string.premium) else getString(R.string.freemium)
                navPremiumStatus?.setOnClickListener {
                    navController?.navigate(NavRoutes.PAYMENT)
                }
            }
        }
    }

    private fun observeAvatar() {
        launchAndRepeatOnStarted {
            mainViewModel.avatarState.collect { bitmap ->
                val navPic = navigationView
                    ?.getHeaderView(0)
                    ?.findViewById<ImageView>(R.id.navProfilePic)
                if (bitmap != null) {
                    navPic?.let { MyAppGlideModule.loadImage(this@MainActivity, bitmap, it) }
                }
            }
        }
    }

    private fun observeAvatarResult() {
        launchAndRepeatOnStarted {
            mainViewModel.effect.collect { effect ->
                if (effect is MainUiEffect.ShowToast) {
                    Toast.makeText(this@MainActivity, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun scheduleLibraryWorker() {
        lifecycleScope.launch {
            val isLoggedIn = mainViewModel.authState.first()
            if (isLoggedIn) {
                WorkManager.getInstance(this@MainActivity).enqueueUniquePeriodicWork(
                    "UpdateEpisodeWork",
                    ExistingPeriodicWorkPolicy.KEEP,
                    PeriodicWorkRequestBuilder<UpdateLibraryWorker>(6, TimeUnit.HOURS).build(),
                )
                WorkManager.getInstance(this@MainActivity)
                    .enqueue(OneTimeWorkRequestBuilder<UpdateLibraryWorker>().build())
            }
        }
    }

    private fun handleDrawerItemSelection() {
        navigationView?.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navLogin -> navController?.navigate(NavRoutes.LOGIN)
                R.id.navLogout -> {
                    mainViewModel.logout()
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

    private fun setupBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController?.popBackStack()?.let {
                    if (!it) {
                        finish()
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    @SuppressLint("InflateParams")
    override fun setupAppBar(container: FrameLayout) {
        container.addView(layoutInflater.inflate(R.layout.app_bar, null), 0)
        val appBarProfile = container.findViewById<ImageFilterButton>(R.id.app_bar_profile)
        val appBarSearch = container.findViewById<ImageButton>(R.id.app_bar_search)
        val appBarNotifications = container.findViewById<ImageButton>(R.id.app_bar_notifications)

        launchAndRepeatOnStarted {
            mainViewModel.avatarState.collect { bitmap ->
                if (bitmap != null) {
                    MyAppGlideModule.loadImage(this@MainActivity, bitmap, appBarProfile)
                } else {
                    appBarProfile.setImageResource(R.drawable.ic_profile)
                }
            }
        }

        appBarProfile.setOnClickListener { navigationView?.let { drawerView -> drawerLayout?.openDrawer(drawerView) } }
        appBarSearch.setOnClickListener { navController?.navigate(NavRoutes.SEARCH) }
        appBarNotifications.setOnClickListener { navController?.let { it1 -> NotificationPopupHelper(it1) }?.show(it) }
    }
}
