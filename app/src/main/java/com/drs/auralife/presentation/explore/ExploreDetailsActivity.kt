package com.drs.auralife.presentation.explore

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.presentation.viewmodel.FilmsViewModel
import com.drs.auralife.data.firebase.realtime.database.category.Category
import com.drs.auralife.data.model.films.Films
import com.drs.auralife.data.model.search.SearchResults
import com.drs.auralife.data.model.films.Pagination
import com.drs.auralife.databinding.ActivityExploreDetailsBinding
import com.drs.auralife.presentation.film.FilmAdapter
import java.util.Locale

private const val CATEGORY_SLUG = "SLUG_CATEGORY"
private const val CATEGORY_NAME = "NAME_CATEGORY"

class ExploreDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityExploreDetailsBinding.inflate(layoutInflater) }
    private val viewModel by lazy { FilmsViewModel(this) }
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

        intent.getStringExtra(CATEGORY_NAME)?.let {
            @SuppressLint("SetTextI18n")
            binding.tvNameApp.text = "${binding.tvNameApp.text} - $it"
        }

        intent.getStringExtra(CATEGORY_SLUG)?.let { slug ->
            isLoading = true
            viewModel.fetchFilmsByCategoryLegacy(slug, 1) { result: SearchResults? ->
                result?.data?.let {
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
                            viewModel.fetchFilmsByCategoryLegacy(slug, pagination.currentPage + 1) { result: SearchResults? ->
                                result?.data?.let {
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

    companion object {
        fun newInstance(
            context: Context,
            category: Category,
        ): Intent {
            val intent = Intent(
                context,
                ExploreDetailsActivity::class.java,
            )
            intent.putExtra(CATEGORY_SLUG, category.slug)
            if (Locale.getDefault().language == "vi") {
                intent.putExtra(CATEGORY_NAME, category.vi)
            } else {
                intent.putExtra(CATEGORY_NAME, category.en)
            }
            return intent
        }
    }
}

