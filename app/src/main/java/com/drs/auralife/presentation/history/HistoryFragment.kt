package com.drs.auralife.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.drs.auralife.R
import com.drs.auralife.databinding.FragmentHistoryBinding
import com.drs.auralife.presentation.AppBarProvider
import com.drs.auralife.presentation.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment :
    Fragment(),
    HistoryFilmAdapter.Listener {
    private val historyViewModel: HistoryViewModel by viewModels()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")
    private val filmAdapter = HistoryFilmAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        (requireActivity() as AppBarProvider).setupAppBar(binding.appBar)
        binding.appBar.findViewById<ImageButton>(R.id.app_bar_search).visibility = View.GONE
        filmAdapter.setCallback(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFilms()
    }

    override fun onResume() {
        super.onResume()
        historyViewModel.loadHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeFilms() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyViewModel.filmsState.collect { state ->
                if (_binding == null) return@collect
                when (state) {
                    is UiState.Success -> {
                        val films = state.data
                        if (films.isEmpty()) {
                            binding.text.visibility = View.VISIBLE
                            filmAdapter.clearItems()
                        } else {
                            binding.text.visibility = View.GONE
                            binding.recyclerView.adapter = filmAdapter
                            filmAdapter.replaceItems(films)
                        }
                    }
                    is UiState.Error -> {
                        binding.text.visibility = View.VISIBLE
                        binding.text.text = state.message
                    }
                    is UiState.Loading -> {}
                }
                }
            }
        }
    }

    override fun onLongClick(slug: String) {
        context?.let { ctx ->
            DeleteHistoryDialog.showDeleteFilmFromHistory(ctx, slug) {
                historyViewModel.deleteHistory(slug)
            }
        }
    }
}
