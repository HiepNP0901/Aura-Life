package com.drs.auralife.presentation.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.presentation.viewmodel.FilmsViewModel
import com.drs.auralife.data.firebase.realtime.database.BannerRepository
import com.drs.auralife.data.model.films.Films
import com.drs.auralife.data.model.search.SearchResults
import com.drs.auralife.data.model.films.Pagination
import com.drs.auralife.databinding.FragmentHomeBinding
import com.drs.auralife.presentation.MainActivity
import com.drs.auralife.presentation.film.FilmAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@dagger.hilt.android.AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: FilmsViewModel by viewModels()
    private var isLoading = false
    private var filmAdapter = FilmAdapter(mutableListOf())

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var paginate: Pagination

    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null

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
        setupBanner()
        setupRecyclerView()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            if (_binding != null) {
                view.isSelected = true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bannerRunnable?.let {
            bannerHandler.removeCallbacks(it)
            bannerHandler.postDelayed(it, 3000)
        }
    }

    override fun onPause() {
        super.onPause()
        view?.isSelected = false
        bannerRunnable?.let { bannerHandler.removeCallbacks(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bannerRunnable?.let { bannerHandler.removeCallbacks(it) }
        _binding = null
    }

    private fun setupBanner() {
        BannerRepository.getBannerData { bannerData ->
            if (_binding == null) return@getBannerData
            val bannerViewPager = binding.bannerViewPager
            bannerViewPager.adapter = BannerAdapter(bannerData)

            bannerRunnable?.let { bannerHandler.removeCallbacks(it) }

            val runnable = object : Runnable {
                override fun run() {
                    if (_binding == null) return
                    val currentItem = bannerViewPager.currentItem
                    val nextItem = if (currentItem == bannerData.size - 1) 0 else currentItem + 1
                    bannerViewPager.setCurrentItem(nextItem, true)
                    bannerHandler.postDelayed(this, 3000)
                }
            }

            bannerRunnable = runnable
            bannerHandler.postDelayed(runnable, 3000)
        }
    }

    private fun setupRecyclerView() {
        if (context?.isConnectedToInternet() == true) {
            binding.recyclerView.apply {
                val displayMetrics = resources.displayMetrics
                var numberFilmInLine = displayMetrics.widthPixels / displayMetrics.densityDpi
                layoutManager = GridLayoutManager(requireContext(), ++numberFilmInLine)
                adapter = filmAdapter
            }

            val layoutManager = binding.recyclerView.layoutManager as GridLayoutManager
            viewModel.fetchLatestFilmsLegacy(1) { result: Films? ->
                if (_binding == null) return@fetchLatestFilmsLegacy
                result?.let {
                    paginate = it.pagination
                    filmAdapter.addItem(it.items)
                    binding.recyclerView.viewTreeObserver.addOnScrollChangedListener {
                        if (_binding == null) return@addOnScrollChangedListener
                        if (paginate.currentPage < paginate.totalPages) {
                            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                            if (!isLoading && lastVisibleItemPosition >= layoutManager.itemCount - 1) {
                                isLoading = true
                                viewModel.fetchLatestFilmsLegacy(paginate.currentPage + 1) { result: Films? ->
                                    if (_binding == null) return@fetchLatestFilmsLegacy
                                    result?.let {
                                        paginate = it.pagination
                                        filmAdapter.addItem(it.items)
                                    }
                                    isLoading = false
                                }
                            }
                        }

                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        if (firstVisibleItemPosition > 0) {
                            binding.scrollToTopButton.visibility = View.VISIBLE
                        } else {
                            binding.scrollToTopButton.visibility = View.GONE
                        }
                    }
                }
            }

            binding.scrollToTopButton.setOnClickListener {
                binding.recyclerView.smoothScrollToPosition(0)
            }
        } else {
            Toast
                .makeText(
                    context,
                    "Vui lòng kết nối mạng và thử lại!!",
                    Toast.LENGTH_LONG,
                ).show()
        }
    }

    fun Context.isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

