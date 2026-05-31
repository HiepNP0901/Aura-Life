package com.drs.auralife.presentation.search

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drs.auralife.presentation.common.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchController(
    private val activity: AppCompatActivity,
    private val searchBar: EditText,
    private val searchLayout: LinearLayout,
    private val searchResults: RecyclerView,
    private val contentContainer: View,
    private val bottomNav: View,
) {
    private val searchViewModel: SearchViewModel by activity.viewModels()
    private val filmAdapter = SearchFilmAdapter()
    private val queryFlow = MutableStateFlow("")
    private var searchIsVisible = false
    private lateinit var textWatcher: TextWatcher

    fun setup() {
        searchResults.layoutManager = LinearLayoutManager(activity)
        searchResults.adapter = filmAdapter
        observeSearchResults()
        setupQueryDebounce()
        setupTextWatcher()
    }

    fun handleBackPress(): Boolean {
        if (searchIsVisible) {
            hideSearch()
            return true
        }
        return false
    }

    fun showSearch() {
        searchLayout.visibility = View.VISIBLE
        contentContainer.visibility = View.GONE
        bottomNav.visibility = View.GONE
        searchBar.requestFocus()
        searchIsVisible = true
    }

    fun destroy() {
        searchBar.removeTextChangedListener(textWatcher)
    }

    private fun hideSearch() {
        searchLayout.visibility = View.GONE
        contentContainer.visibility = View.VISIBLE
        bottomNav.visibility = View.VISIBLE
        searchIsVisible = false
        filmAdapter.replaceItems(emptyList())
        searchBar.text.clear()
        queryFlow.value = ""
    }

    private fun observeSearchResults() {
        activity.lifecycleScope.launch {
            activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.searchResultsState.collect { state ->
                    if (state is UiState.Success) {
                        filmAdapter.replaceItems(state.data)
                    }
                }
            }
        }
    }

    private fun setupQueryDebounce() {
        activity.lifecycleScope.launch {
            activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
                queryFlow
                    .debounce(500)
                    .distinctUntilChanged()
                    .filter { it.isNotEmpty() }
                    .collectLatest { query ->
                        searchViewModel.searchFilms(query, 5)
                    }
            }
        }
    }

    private fun setupTextWatcher() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                queryFlow.value = query
                if (query.isEmpty()) {
                    filmAdapter.replaceItems(emptyList())
                }
            }
        }
        searchBar.addTextChangedListener(textWatcher)
    }
}
