package com.drs.auralife.presentation.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.R
import com.drs.auralife.databinding.FragmentHomeBinding
import com.drs.auralife.presentation.AppBarProvider
import com.drs.auralife.presentation.home.adapter.HomeFilmAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")

    private val homeViewModel: HomeViewModel by viewModels()
    private val filmAdapter = HomeFilmAdapter { slug ->
        homeViewModel.onFilmClicked(slug)
    }

    private var isLoading = false
    private var currentPage = 1
    private var totalPages = 0
    private var autoScrollJob: Job? = null
    private var scrollListener: androidx.recyclerview.widget.RecyclerView.OnScrollListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        (requireActivity() as AppBarProvider).setupAppBar(binding.appBar)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        observeEffect()
        homeViewModel.loadBanners()
        setupRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            if (_binding != null) view.isSelected = true
        }
    }

    override fun onDestroyView() {
        autoScrollJob?.cancel()
        scrollListener?.let { binding.recyclerView.removeOnScrollListener(it) }
        scrollListener = null
        _binding = null
        super.onDestroyView()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.state.collect { state ->
                    if (_binding == null) return@collect

                    if (state.banners.isNotEmpty()) {
                        val bannerViewPager = binding.bannerViewPager
                        bannerViewPager.adapter = com.drs.auralife.presentation.home.adapter.BannerAdapter(state.banners) { slug ->
                            homeViewModel.onFilmClicked(slug)
                        }
                        startAutoScroll(bannerViewPager, state.banners.size)
                    }

                    filmAdapter.submitList(state.films)
                }
            }
        }
    }

    private fun observeEffect() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.effect.collect { effect ->
                    when (effect) {
                        is HomeUiEffect.ShowToast -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is HomeUiEffect.NavigateToFilm -> {
                            val bundle = Bundle().apply { putString("slug", effect.slug) }
                            findNavController().navigate(R.id.film_details, bundle)
                        }
                    }
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

            scrollListener = object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    if (_binding == null) return
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    if (currentPage < totalPages) {
                        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                        if (!isLoading && lastVisibleItemPosition >= layoutManager.itemCount - 1) {
                            isLoading = true
                            currentPage++
                            homeViewModel.loadMoreLatestFilms(currentPage)
                        }
                    }
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    binding.scrollToTopButton.visibility =
                        if (firstVisibleItemPosition > 0) View.VISIBLE else View.GONE
                }
            }
            scrollListener?.let { binding.recyclerView.addOnScrollListener(it) }

            binding.scrollToTopButton.setOnClickListener {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        } else {
            Toast.makeText(context, getString(R.string.no_internet_retry), Toast.LENGTH_LONG).show()
        }
    }

    private fun observeLatestFilms() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.state.collect { state ->
                    totalPages = state.totalPages
                    isLoading = state.isLoadingMore
                }
            }
        }
    }

    private fun startAutoScroll(bannerViewPager: androidx.viewpager2.widget.ViewPager2, count: Int) {
        autoScrollJob?.cancel()
        autoScrollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                delay(3000)
                if (_binding == null) break
                val currentItem = bannerViewPager.currentItem
                val nextItem = if (currentItem == count - 1) 0 else currentItem + 1
                bannerViewPager.setCurrentItem(nextItem, true)
            }
        }
    }

    private fun Context.isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
