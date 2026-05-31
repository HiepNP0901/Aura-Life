package com.drs.auralife.presentation.explore

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.drs.auralife.domain.model.Category
import com.drs.auralife.databinding.ActivityExploreDetailsBinding
import com.drs.auralife.domain.model.Film
import java.util.Locale
import kotlinx.coroutines.launch

private const val CATEGORY_SLUG = "SLUG_CATEGORY"
private const val CATEGORY_NAME = "NAME_CATEGORY"

@dagger.hilt.android.AndroidEntryPoint
class ExploreDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityExploreDetailsBinding.inflate(layoutInflater) }
    private val viewModel: ExploreDetailViewModel by viewModels()
    private val filmAdapter by lazy { ExploreFilmAdapter(mutableListOf()) }
    private var isLoading = false
    private var currentPage = 1
    private var totalPages = 0
    private var currentSlug: String? = null
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null

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

        currentSlug = intent.getStringExtra(CATEGORY_SLUG)
        currentSlug?.let { slug ->
            observeCategoryFilms()
            viewModel.getFilmsByCategory(slug, 1)
        }
        setupPagination()
    }

    private fun observeCategoryFilms() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryFilmsState.collect { films ->
                    filmAdapter.replaceItems(films)
                    isLoading = false
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryTotalPages.collect { pages ->
                    totalPages = pages
                }
            }
        }
    }

    private fun setupPagination() {
        scrollListener = ViewTreeObserver.OnScrollChangedListener {
            val lastVisibleItemPosition =
                (binding.recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            if (lastVisibleItemPosition >= filmAdapter.itemCount - 1 && currentPage < totalPages && !isLoading) {
                isLoading = true
                currentPage++
                currentSlug?.let { slug ->
                    viewModel.loadMoreFilmsByCategory(slug, currentPage)
                }
            }
        }
        scrollListener?.let { binding.recyclerView.viewTreeObserver.addOnScrollChangedListener(it) }
    }

    override fun onRestart() {
        super.onRestart()
        lifecycleScope.launch {
            kotlinx.coroutines.delay(3000)
            if (isDestroyed) return@launch
            binding.root.isSelected = true
        }
    }

    override fun onPause() {
        super.onPause()
        binding.root.isSelected = false
    }

    override fun onDestroy() {
        super.onDestroy()
        scrollListener?.let { binding.recyclerView.viewTreeObserver.removeOnScrollChangedListener(it) }
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
                intent.putExtra(CATEGORY_NAME, category.name)
            } else {
                intent.putExtra(CATEGORY_NAME, category.localizedName)
            }
            return intent
        }
    }
}
