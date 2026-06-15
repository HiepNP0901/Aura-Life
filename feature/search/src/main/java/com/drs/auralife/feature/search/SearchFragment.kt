package com.drs.auralife.feature.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.drs.auralife.core.navigation.AppNavigator
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import com.drs.auralife.feature.search.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val appNavigator by lazy { AppNavigator(findNavController()) }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")

    private val searchViewModel: SearchViewModel by viewModels()
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
            appNavigator.navigateToFilmDetails(slug)
        }
        binding.searchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.searchResults.adapter = searchAdapter

        binding.searchBar.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    searchViewModel.setSearchQuery(s.toString().trim())
                }
            },
        )

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
                        appNavigator.navigateToFilmDetails(effect.slug)
                    }
                }
            }
        }
    }
}
