package com.drs.auralife.presentation.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.drs.auralife.feature.search.R
import com.drs.auralife.feature.search.databinding.FragmentSearchBinding
import com.drs.auralife.presentation.common.launchAndRepeatWithViewLifecycle
import com.drs.auralife.presentation.navigation.NavRoutes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")

    private val searchViewModel: SearchViewModel by viewModels()
    private val searchQueryFlow = MutableStateFlow("")
    private var searchAdapter: SearchAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearch()
        binding.searchBar.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSearch() {
        searchAdapter = SearchAdapter { slug ->
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.search, true)
                .build()
            findNavController().navigate(NavRoutes.filmDetails(slug), navOptions)
        }
        binding.searchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResults.adapter = searchAdapter

        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQueryFlow.value = s.toString().trim()
                if (s.toString().trim().isEmpty()) {
                    searchViewModel.clearResults()
                    searchAdapter?.replaceItems(emptyList())
                }
            }
        })

        launchAndRepeatWithViewLifecycle {
            searchViewModel.state.collect { state ->
                when (state) {
                    is SearchUiState.Idle -> {
                        binding.loadingIndicator.visibility = View.GONE
                        binding.errorText.visibility = View.GONE
                    }
                    is SearchUiState.Loading -> {
                        binding.loadingIndicator.visibility = View.VISIBLE
                        binding.errorText.visibility = View.GONE
                    }
                    is SearchUiState.Success -> {
                        binding.loadingIndicator.visibility = View.GONE
                        binding.errorText.visibility = View.GONE
                        searchAdapter?.replaceItems(state.films)
                    }
                    is SearchUiState.Error -> {
                        binding.loadingIndicator.visibility = View.GONE
                        binding.errorText.apply {
                            visibility = View.VISIBLE
                            text = state.message
                        }
                    }
                }
            }
        }

        launchAndRepeatWithViewLifecycle {
            searchViewModel.effect.collect { effect ->
                when (effect) {
                    is SearchUiEffect.NavigateToFilmDetails -> {
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.search, true)
                            .build()
                        findNavController().navigate(NavRoutes.filmDetails(effect.slug), navOptions)
                    }
                }
            }
        }

        lifecycleScope.launch {
            searchQueryFlow
                .debounce(500)
                .distinctUntilChanged()
                .filter { it.isNotEmpty() }
                .collectLatest { query ->
                    searchViewModel.searchFilms(query, 5)
                }
        }
    }
}
