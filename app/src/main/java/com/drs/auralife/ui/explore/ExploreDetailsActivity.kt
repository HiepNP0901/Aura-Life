package com.drs.auralife.ui.explore

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.model.films.Pagination
import com.drs.auralife.databinding.ActivityExploreDetailsBinding
import com.drs.auralife.ui.film.FilmAdapter

const val CATEGORY_SLUG = "@categorySlug"

class ExploreDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityExploreDetailsBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ViewModelProvider(
            this, FilmViewModelFactory(this)
        )[FilmsViewModel::class.java]
    }
    private val filmAdapter by lazy { FilmAdapter(mutableListOf()) }
    private var isLoading = false
    private lateinit var pagination: Pagination

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.apply {
            val displayMetrics = resources.displayMetrics
            var numberFilmInLine = displayMetrics.widthPixels / displayMetrics.densityDpi
            layoutManager = GridLayoutManager(this@ExploreDetailsActivity, ++numberFilmInLine)
            adapter = filmAdapter
        }

        intent.getStringExtra(CATEGORY_SLUG)?.let { slug ->
            isLoading = true
            viewModel.fetchFilmsByCategory(slug, 1) {
                it?.data?.let {
                    for (item in it.items) {
                        item.posterUrl = it.appDomainCdnImage + "/" + item.posterUrl
                        item.thumbUrl = it.appDomainCdnImage + "/" + item.thumbUrl
                    }
                    filmAdapter.addItem(it.items)
                    pagination = it.params.pagination
                    isLoading = false

                    binding.recyclerView.viewTreeObserver.addOnScrollChangedListener {
                        val lastVisibleItemPosition =
                            (binding.recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                        if (lastVisibleItemPosition >= filmAdapter.itemCount - 1 && pagination.currentPage < pagination.totalPages && !isLoading) {
                            isLoading = true
                            viewModel.fetchFilmsByCategory(slug, pagination.currentPage + 1) {
                                it?.data?.let {
                                    for (item in it.items) {
                                        item.posterUrl = it.appDomainCdnImage + "/" + item.posterUrl
                                        item.thumbUrl = it.appDomainCdnImage + "/" + item.thumbUrl
                                    }
                                    filmAdapter.addItem(it.items)
                                    pagination = it.params.pagination
                                    isLoading = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onRestart() {
        super.onRestart()
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            binding.root.isSelected = true
        }, 3000)
    }


    override fun onPause() {
        super.onPause()
        binding.root.isSelected = false
    }
}