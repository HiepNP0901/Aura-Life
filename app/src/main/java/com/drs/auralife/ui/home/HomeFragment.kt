package com.drs.auralife.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.firebase.RealtimeDB
import com.drs.auralife.data.model.films.Pagination
import com.drs.auralife.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private var isLoading = false
    private lateinit var filmAdapter: FilmAdapter
    private lateinit var viewModel: FilmsViewModel
    private lateinit var paginate: Pagination
    private var numberFilmInLine = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            FilmViewModelFactory(requireContext())
        )[FilmsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val recyclerView = binding.recyclerView

        numberFilmInLine = (resources.displayMetrics.widthPixels)/360
        recyclerView.layoutManager = GridLayoutManager(requireContext(), numberFilmInLine)

        filmAdapter = FilmAdapter(mutableListOf(), numberFilmInLine)
        recyclerView.adapter = filmAdapter

        viewModel.fetchLatestFilms(1) {
            it?.let{
                paginate = it.pagination
                filmAdapter.addItem(it.items)
                loadMoreFilm()
            }
        }

        addBanner()

        return binding.root
    }

    private fun loadMoreFilm() {

        binding.recyclerView.viewTreeObserver.addOnScrollChangedListener {

            if (paginate.currentPage < paginate.totalPages) {

                val layoutManager = binding.recyclerView.layoutManager as GridLayoutManager

                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItemPosition >= layoutManager.itemCount - 1) {

                    isLoading = true

                    viewModel.fetchLatestFilms(paginate.currentPage + 1) {
                        it?.let{
                            paginate = it.pagination
                            filmAdapter.addItem(it.items)
                        }
                        isLoading = false
                    }
                }
            }
        }
    }

    private fun addBanner() {

        RealtimeDB.getBannerData { bannerData ->

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
}
