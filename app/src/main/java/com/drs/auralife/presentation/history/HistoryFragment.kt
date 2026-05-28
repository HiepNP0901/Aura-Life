package com.drs.auralife.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.R
import com.drs.auralife.databinding.FragmentHistoryBinding
import com.drs.auralife.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryFragment :
    Fragment(),
    HistoryFilmAdapter.Listener {
    private val historyViewModel: HistoryViewModel by viewModels()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val filmAdapter = HistoryFilmAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        (requireActivity() as MainActivity).setupAppBar(binding.appBar)
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
        context?.let { historyViewModel.loadHistory(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeFilms() {
        lifecycleScope.launch {
            historyViewModel.filmsState.collect { films ->
                if (_binding == null) return@collect
                if (films.isEmpty()) {
                    binding.text.visibility = View.VISIBLE
                    filmAdapter.clearItems()
                } else {
                    binding.text.visibility = View.GONE
                    binding.recyclerView.adapter = filmAdapter
                    filmAdapter.replaceItems(films)
                }
            }
        }
    }

    override fun onLongClick(slug: String) {
        context?.let { ctx ->
            DeleteHistoryDialog.showDeleteFilmFromHistory(ctx, slug) {
                historyViewModel.deleteHistory(ctx, slug)
            }
        }
    }
}
