package com.drs.auralife.feature.explore.explore_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.navigation.NavRoutes
import com.drs.auralife.feature.explore.databinding.FragmentExploreDetailsBinding
import com.drs.auralife.feature.explore.explore_details.adapter.ExploreDetailFilmAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreDetailFragment : Fragment() {

    private val exploreDetailViewModel: ExploreDetailViewModel by viewModels()
    private var _binding: FragmentExploreDetailsBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")
    private var slug: String = ""
    private var name: String = ""

    private val filmAdapter = ExploreDetailFilmAdapter { slug ->
        exploreDetailViewModel.onFilmClicked(slug)
    }

    private var scrollListener: RecyclerView.OnScrollListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExploreDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slug = requireArguments().getString("slug") ?: return
        name = requireArguments().getString("name") ?: return

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvCategoryName.text = name

        binding.recyclerView.apply {
            val displayMetrics = resources.displayMetrics
            var numberFilmInLine = displayMetrics.widthPixels / displayMetrics.densityDpi
            layoutManager = GridLayoutManager(requireContext(), ++numberFilmInLine)
            adapter = filmAdapter
        }

        observeState()
        observeEffect()
        exploreDetailViewModel.getFilmsByCategory(slug)
        setupPagination()
    }

    override fun onDestroy() {
        super.onDestroy()
        scrollListener?.let { binding.recyclerView.removeOnScrollListener(it) }
    }

    private fun observeState() {
        launchAndRepeatWithViewLifecycle {
                exploreDetailViewModel.state.collect { state ->
                    filmAdapter.submitList(state.films)
                }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                exploreDetailViewModel.effect.collect { effect ->
                    when (effect) {
                        is ExploreDetailUiEffect.ShowToast -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is ExploreDetailUiEffect.NavigateToFilm -> {
                            findNavController().navigate(NavRoutes.filmDetails(effect.slug))
                        }
                    }
                }
        }
    }

    private fun setupPagination() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisibleItemPosition = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                if (lastVisibleItemPosition >= filmAdapter.itemCount - 1) {
                    exploreDetailViewModel.onScrolledToBottom(slug)
                }
            }
        }
        scrollListener?.let { binding.recyclerView.addOnScrollListener(it) }
    }
}

