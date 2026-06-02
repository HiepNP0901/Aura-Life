package com.drs.auralife.presentation.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.feature.explore.R
import com.drs.auralife.core.designsystem.R as DsR
import com.drs.auralife.navigation.NavRoutes
import com.drs.auralife.feature.explore.databinding.FragmentExploreBinding
import com.drs.auralife.designsystem.AppBarProvider
import com.drs.auralife.presentation.explore.adapter.ExploreFilmAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ExploreFragment : Fragment() {
    private val exploreViewModel: ExploreViewModel by viewModels()
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        (requireActivity() as AppBarProvider).setupAppBar(binding.appBar)
        exploreViewModel.loadCategories()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        observeEffect()
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

    private fun observeState() {
        launchAndRepeatWithViewLifecycle {
                exploreViewModel.state.collect { state ->
                    if (_binding == null) return@collect
                    if (state.isLoading) {
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
                    if (state.categories.isNotEmpty()) {
                        buildCategoryViews(state.categories)
                    }
                    state.filmsByCategory.forEach { (slug, films) ->
                        val index = state.categories.indexOfFirst { it.slug == slug }
                        if (index >= 0) {
                            val child = binding.exploreFragmentBody.getChildAt(index)
                            val rv = child?.findViewById<RecyclerView>(R.id.recyclerView)
                            (rv?.adapter as? ExploreFilmAdapter)?.addItem(films)
                        }
                    }
                }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                exploreViewModel.effect.collect { effect ->
                    when (effect) {
                        is ExploreUiEffect.ShowToast -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is ExploreUiEffect.NavigateToCategory -> {
                            findNavController().navigate(NavRoutes.exploreDetails(effect.slug, effect.name))
                        }
                        is ExploreUiEffect.NavigateToFilm -> {
                            findNavController().navigate(NavRoutes.filmDetails(effect.slug))
                        }
                    }
                }
        }
    }

    private fun buildCategoryViews(categories: List<com.drs.auralife.domain.model.Category>) {
        binding.exploreFragmentBody.removeAllViews()
        categories.forEach { category ->
            val item = layoutInflater.inflate(DsR.layout.horizontal_film_list, null)

            item.findViewById<AppCompatButton>(DsR.id.buttonHorizontalFilmList).setOnClickListener {
                exploreViewModel.onCategoryClicked(category.slug, category.name)
            }

            val filmAdapter = ExploreFilmAdapter { slug ->
                exploreViewModel.onFilmClicked(slug)
            }
            item.findViewById<RecyclerView>(R.id.recyclerView).adapter = filmAdapter
            binding.exploreFragmentBody.addView(item)

            val buttonText = if (Locale.getDefault().language == "en") category.localizedName else category.name
            item.findViewById<AppCompatButton>(DsR.id.buttonHorizontalFilmList).text = buttonText
        }
    }
}

