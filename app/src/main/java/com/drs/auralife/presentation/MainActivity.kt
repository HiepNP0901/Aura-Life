package com.drs.auralife.presentation

import android.annotation.SuppressLint
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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.drs.auralife.R
import com.drs.auralife.presentation.common.MyAppGlideModule
import com.drs.auralife.core.util.Notification
import com.drs.auralife.presentation.common.PermissionPhotoHandler
import com.drs.auralife.core.worker.UpdateLibraryWorker
import com.drs.auralife.presentation.common.NotificationAdapter
import com.drs.auralife.presentation.navigation.NavRoutes
import com.drs.auralife.presentation.search.SearchFilmAdapter
import com.drs.auralife.presentation.search.SearchUiEffect
import com.drs.auralife.presentation.search.SearchUiState
import com.drs.auralife.presentation.search.SearchViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

    @dagger.hilt.android.AndroidEntryPoint
@OptIn(FlowPreview::class)
class MainActivity : AppCompatActivity(), AppBarProvider {
    companion object {
        private const val PREF_NAME = "PREFERENCE"
    }

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var bottomNavigationView: BottomNavigationView

    private var permissionPhotoHandler: PermissionPhotoHandler? = null
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var pageChangeCallback: androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback? = null

    private val mainViewModel: MainViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()

    private var searchBar: EditText? = null
    private var searchLayout: LinearLayout? = null
    private var searchResults: RecyclerView? = null
    private var searchAdapter: SearchFilmAdapter? = null
    private val searchQueryFlow = kotlinx.coroutines.flow.MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        drawerLayout = findViewById(R.id.main_layout)
        navigationView = findViewById(R.id.navigation_view)
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        searchLayout = findViewById(R.id.search_layout)
        searchBar = findViewById(R.id.search_bar)
        searchResults = findViewById(R.id.search_results)

        setupBottomNav()
        setupDrawer()
        setupBackPressed()
        hideBottomNavOnDetailScreens()
        setupSearch()
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

    private fun setupBottomNav() {
        bottomNavigationView.setupWithNavController(navController)
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
                    navController.navigate(R.id.login)
                }
            }

        val navLogin = navigationView.menu.findItem(R.id.navLogin)
        val navLogout = navigationView.menu.findItem(R.id.navLogout)
        val navigationHeader = navigationView.getHeaderView(0)
        val navEmail = navigationHeader.findViewById<TextView>(R.id.navEmail)
        val navPic = navigationHeader.findViewById<ImageView>(R.id.navProfilePic)
        val navPremiumStatus = navigationHeader.findViewById<TextView>(R.id.navFreemium)
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
            val isLoggedIn = mainViewModel.authState.first()
            if (isLoggedIn) {
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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.premiumStatus.collect { status ->
                    if (status == null) return@collect
                    sharedPreferences.edit { putString("ExpireDate", status.expiryTimestamp?.toString() ?: "") }
                    navPremiumStatus.text = if (status.isPremium) getString(R.string.premium) else getString(R.string.freemium)
                    navPremiumStatus.setOnClickListener {
                        navController.navigate(R.id.payment)
                    }
                }
            }
        }
    }

    private fun handleDrawerItemSelection() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navLogin -> navController.navigate(R.id.login)
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

    private fun observeAvatarResult() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.effect.collect { effect ->
                    when (effect) {
                        is MainUiEffect.ShowToast -> {
                            Toast.makeText(this@MainActivity, effect.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun setupBackPressed() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (searchLayout?.visibility == View.VISIBLE) {
                    hideSearch()
                    return
                }
                if (!navController.popBackStack()) {
                    finish()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun hideBottomNavOnDetailScreens() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val detailDestinations = setOf(
                R.id.film_details, R.id.library_details, R.id.payment,
                R.id.play_film, R.id.explore_details, R.id.login, R.id.register,
            )
            bottomNavigationView.visibility =
                if (destination.id in detailDestinations) View.GONE else View.VISIBLE
        }
    }

    @SuppressLint("InflateParams")
    override fun setupAppBar(view: FrameLayout) {
        view.addView(layoutInflater.inflate(R.layout.app_bar, null), 0)
        val appBarProfile = view.findViewById<ImageFilterButton>(R.id.app_bar_profile)
        val appBarSearch = view.findViewById<ImageButton>(R.id.app_bar_search)
        val appBarNotifications = view.findViewById<ImageButton>(R.id.app_bar_notifications)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.avatarState.collect { bitmap ->
                    if (bitmap != null) {
                        MyAppGlideModule.loadImage(this@MainActivity, bitmap, appBarProfile)
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
            showSearch()
        }

        appBarNotifications.setOnClickListener {
            showNotificationList(it)
        }
    }

    private fun setupSearch() {
        searchAdapter = SearchFilmAdapter { slug ->
            hideSearch()
            val bundle = Bundle().apply { putString("slug", slug) }
            navController.navigate(R.id.film_details, bundle)
        }
        searchResults?.layoutManager = LinearLayoutManager(this)
        searchResults?.adapter = searchAdapter

        val textWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                searchQueryFlow.value = s.toString().trim()
                if (s.toString().trim().isEmpty()) {
                    searchViewModel.clearResults()
                    searchAdapter?.replaceItems(emptyList())
                }
            }
        }
        searchBar?.addTextChangedListener(textWatcher)

        observeSearchState()
        observeSearchEffect()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchQueryFlow
                    .debounce(500)
                    .distinctUntilChanged()
                    .filter { it.isNotEmpty() }
                    .collectLatest { query ->
                        searchViewModel.searchFilms(query, 5)
                    }
            }
        }
    }

    private fun observeSearchState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.state.collect { state ->
                    when (state) {
                        is SearchUiState.Success -> {
                            searchAdapter?.replaceItems(state.films)
                        }
                        else -> { /* idle, loading, error handled implicitly */ }
                    }
                }
            }
        }
    }

    private fun observeSearchEffect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.effect.collect { effect ->
                    when (effect) {
                        is SearchUiEffect.NavigateToFilmDetails -> {
                            hideSearch()
                            val bundle = Bundle().apply { putString("slug", effect.slug) }
                            navController.navigate(R.id.film_details, bundle)
                        }
                    }
                }
            }
        }
    }

    private fun showSearch() {
        searchLayout?.visibility = View.VISIBLE
        bottomNavigationView.visibility = View.GONE
        searchBar?.requestFocus()
        searchResults?.adapter = searchAdapter
    }

    private fun hideSearch() {
        searchLayout?.visibility = View.GONE
        bottomNavigationView.visibility = View.VISIBLE
        searchBar?.text?.clear()
        searchViewModel.clearResults()
        searchAdapter?.replaceItems(emptyList())
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
                val bundle = Bundle().apply {
                    putString("slug", it.first)
                }
                navController.navigate(R.id.film_details, bundle)
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
