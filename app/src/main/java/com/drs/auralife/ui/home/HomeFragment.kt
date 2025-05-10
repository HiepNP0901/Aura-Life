package com.drs.auralife.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.firebase.realtime.database.BannerRepository
import com.drs.auralife.data.model.films.Pagination
import com.drs.auralife.databinding.FragmentHomeBinding
import com.drs.auralife.ui.MainActivity
import com.drs.auralife.ui.film.FilmAdapter

class HomeFragment : Fragment() {
    private var isLoading = false
    private var filmAdapter = FilmAdapter(mutableListOf())
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private lateinit var paginate: Pagination

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
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
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            view.isSelected = true
        }, 3000)
    }

    override fun onPause() {
        super.onPause()
        view?.isSelected = false
    }

    private fun setupBanner() {
        BannerRepository.getBannerData { bannerData ->
            val bannerViewPager = binding.bannerViewPager
            bannerViewPager.adapter = BannerAdapter(bannerData)
            val handler = Handler(Looper.getMainLooper())

            val runnable = object : Runnable {
                override fun run() {
                    val currentItem = bannerViewPager.currentItem
                    val nextItem = if (currentItem == bannerData.size - 1) 0 else currentItem + 1
                    bannerViewPager.setCurrentItem(nextItem, true)
                    handler.postDelayed(this, 3000)
                }
            }

            handler.postDelayed(runnable, 3000)
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
            val viewModel = FilmsViewModel(requireContext())
            viewModel.fetchLatestFilms(1) {
                it?.let {
                    paginate = it.pagination
                    filmAdapter.addItem(it.items)
                    binding.recyclerView.viewTreeObserver.addOnScrollChangedListener {
                        if (paginate.currentPage < paginate.totalPages) {
                            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                            if (!isLoading && lastVisibleItemPosition >= layoutManager.itemCount - 1) {
                                isLoading = true
                                viewModel.fetchLatestFilms(paginate.currentPage + 1) {
                                    it?.let {
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
