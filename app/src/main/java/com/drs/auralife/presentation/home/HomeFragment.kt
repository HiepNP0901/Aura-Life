package com.drs.auralife.presentation.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.R
import com.drs.auralife.databinding.FragmentHomeBinding
import com.drs.auralife.presentation.MainActivity
import com.drs.auralife.presentation.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var isLoading = false
    private var currentPage = 1
    private var totalPages = 0
    private val filmAdapter = HomeFilmAdapter(mutableListOf())

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).setupAppBar(binding.appBar)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeBanners()
        homeViewModel.loadBanners()
        setupRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(FOCUS_DELAY_MS)
            if (_binding != null) {
                view.isSelected = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bannerRunnable?.let {
            bannerHandler.removeCallbacks(it)
            bannerHandler.postDelayed(it, BANNER_SCROLL_INTERVAL_MS)
        }
    }

    override fun onPause() {
        super.onPause()
        view?.isSelected = false
        bannerRunnable?.let { bannerHandler.removeCallbacks(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollListener?.let { binding.recyclerView.viewTreeObserver.removeOnScrollChangedListener(it) }
        scrollListener = null
        _binding = null
    }

    private fun observeBanners() {
        lifecycleScope.launch {
            homeViewModel.bannersState.collect { state ->
                if (_binding == null) return@collect
                when (state) {
                    is UiState.Success -> {
                        val bannerData = state.data
                        if (bannerData.isEmpty()) return@collect
                        val bannerViewPager = binding.bannerViewPager
                        bannerViewPager.adapter = BannerAdapter(bannerData)

                        bannerRunnable?.let { bannerHandler.removeCallbacks(it) }

                        val runnable = object : Runnable {
                            override fun run() {
                                if (_binding == null) return
                                val currentItem = bannerViewPager.currentItem
                                val nextItem = if (currentItem == bannerData.size - 1) 0 else currentItem + 1
                                bannerViewPager.setCurrentItem(nextItem, true)
                                bannerHandler.postDelayed(this, BANNER_SCROLL_INTERVAL_MS)
                            }
                        }

                        bannerRunnable = runnable
                        bannerHandler.postDelayed(runnable, BANNER_SCROLL_INTERVAL_MS)
                    }
                    is UiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }

    private fun setupRecyclerView() {
        if (context?.isConnectedToInternet() == true) {
            binding.recyclerView.apply {
                val displayMetrics = resources.displayMetrics
                var numberFilmInLine = (displayMetrics.widthPixels / 400).coerceIn(2, 5)
                layoutManager = GridLayoutManager(requireContext(), ++numberFilmInLine)
                adapter = filmAdapter
            }

            observeLatestFilms()

            currentPage = 1
            homeViewModel.getLatestFilms(currentPage)

            val layoutManager = binding.recyclerView.layoutManager as GridLayoutManager
            scrollListener = ViewTreeObserver.OnScrollChangedListener {
                if (_binding == null) return@OnScrollChangedListener
                if (currentPage < totalPages) {
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                    if (!isLoading && lastVisibleItemPosition >= layoutManager.itemCount - 1) {
                        isLoading = true
                        currentPage++
                        homeViewModel.loadMoreLatestFilms(currentPage)
                    }
                }

                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition > 0) {
                    binding.scrollToTopButton.visibility = View.VISIBLE
                } else {
                    binding.scrollToTopButton.visibility = View.GONE
                }
            }
            binding.recyclerView.viewTreeObserver.addOnScrollChangedListener(scrollListener)

            binding.scrollToTopButton.setOnClickListener {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        } else {
            Toast
                .makeText(
                    context,
                    getString(R.string.no_internet_retry),
                    Toast.LENGTH_LONG,
                ).show()
        }
    }

    private fun observeLatestFilms() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.latestFilmsState.collect { state ->
                    when (state) {
                        is UiState.Success -> {
                            filmAdapter.replaceItems(state.data.films)
                            totalPages = state.data.totalPages
                        }
                        is UiState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        }
                        is UiState.Loading -> {}
                    }
                    isLoading = false
                }
            }
        }
    }

    fun Context.isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        private const val FOCUS_DELAY_MS = 3000L
        private const val BANNER_SCROLL_INTERVAL_MS = 3000L
    }
}
