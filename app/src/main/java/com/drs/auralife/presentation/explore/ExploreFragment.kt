package com.drs.auralife.presentation.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.R
import com.drs.auralife.presentation.viewmodel.FilmsViewModel
import com.drs.auralife.data.firebase.realtime.database.category.CategoryRepository
import com.drs.auralife.data.model.search.SearchResults
import com.drs.auralife.databinding.FragmentExploreBinding
import com.drs.auralife.presentation.MainActivity
import com.drs.auralife.presentation.film.FilmAdapter
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@dagger.hilt.android.AndroidEntryPoint
class ExploreFragment : Fragment() {
    private val viewModel: FilmsViewModel by viewModels()
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).setupAppBar(binding.appBar)

        CategoryRepository.getCategoryData { categories ->
            if (_binding == null) return@getCategoryData
            categories.forEach { category ->
                val currentContext = context ?: return@forEach
                val item = layoutInflater.inflate(R.layout.horizontal_film_list, null)

                item
                    .findViewById<AppCompatButton>(R.id.buttonHorizontalFilmList)
                    .setOnClickListener {
                        startActivity(ExploreDetailsActivity.newInstance(currentContext, category))
                    }

                val filmAdapter = FilmAdapter(mutableListOf())
                item.findViewById<RecyclerView>(R.id.recyclerView).adapter = filmAdapter
                binding.exploreFragmentBody.addView(item)
                if (Locale.getDefault().language == "en") {
                    item.findViewById<AppCompatButton>(R.id.buttonHorizontalFilmList).text =
                        category.en
                } else if (Locale.getDefault().language == "vi") {
                    item.findViewById<AppCompatButton>(R.id.buttonHorizontalFilmList).text =
                        category.vi
                }

                viewModel.fetchFilmsByCategoryLegacy(category.slug, 1) { result: SearchResults? ->
                    if (_binding == null) return@fetchFilmsByCategoryLegacy
                    result?.data?.let { data ->
                        for (movie in data.items) {
                            movie.posterUrl = data.appDomainCdnImage + "/" + movie.posterUrl
                            movie.thumbUrl = data.appDomainCdnImage + "/" + movie.thumbUrl
                        }

                        filmAdapter.addItem(data.items)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            _binding?.root?.isSelected = true
        }
    }

    override fun onPause() {
        super.onPause()
        _binding?.root?.isSelected = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

