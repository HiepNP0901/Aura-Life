package com.drs.auralife.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.feature.home.R
import com.drs.auralife.navigation.NavRoutes
import com.drs.auralife.feature.home.databinding.FragmentHomeBinding
import com.drs.auralife.designsystem.AppBarProvider
import com.drs.auralife.presentation.home.adapter.BannerAdapter
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
        launchAndRepeatWithViewLifecycle {
                homeViewModel.state.collect { state ->
                    if (_binding == null) return@collect

                    if (state.isLoadingBanners || state.isLoadingFilms) {
                        binding.loadingIndicator.visibility = View.VISIBLE
                    } else {
                        binding.loadingIndicator.visibility = View.GONE
                    }
                    if (state.errorMessage != null) {
                        binding.errorText.apply {
                            visibility = View.VISIBLE
                            text = state.errorMessage
                        }
                    } else {
                        binding.errorText.visibility = View.GONE
                    }

                    if (state.banners.isNotEmpty()) {
                        val bannerViewPager = binding.bannerViewPager
                        bannerViewPager.adapter = BannerAdapter(state.banners) { slug ->
                            homeViewModel.onFilmClicked(slug)
                        }
                        startAutoScroll(bannerViewPager, state.banners.size)
                    }

                    filmAdapter.submitList(state.films)
                }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                homeViewModel.effect.collect { effect ->
                    when (effect) {
                        is HomeUiEffect.ShowToast -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is HomeUiEffect.NavigateToFilm -> {
                            findNavController().navigate(NavRoutes.filmDetails(effect.slug))
                        }
                    }
                }
        }
    }

    private fun setupRecyclerView() {
        val isConnected = homeViewModel.checkConnectivity(requireContext())
        if (!isConnected) {
            Toast.makeText(context, getString(R.string.no_internet_retry), Toast.LENGTH_LONG).show()
            return
        }
        binding.recyclerView.apply {
            val displayMetrics = resources.displayMetrics
            var numberFilmInLine = (displayMetrics.widthPixels / 400).coerceIn(2, 5)
            layoutManager = GridLayoutManager(requireContext(), ++numberFilmInLine)
            adapter = filmAdapter
        }

        homeViewModel.loadLatestFilms()

        scrollListener = object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                if (_binding == null) return
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisibleItemPosition >= layoutManager.itemCount - 1) {
                    homeViewModel.onScrolledToBottom()
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

}

