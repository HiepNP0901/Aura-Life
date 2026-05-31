package com.drs.auralife.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drs.auralife.presentation.common.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.R
import com.drs.auralife.presentation.navigation.NavRoutes
import com.drs.auralife.databinding.FragmentHistoryBinding
import com.drs.auralife.presentation.AppBarProvider
import com.drs.auralife.presentation.history.adapter.HistoryFilmAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private val historyViewModel: HistoryViewModel by viewModels()
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")
    private val filmAdapter = HistoryFilmAdapter(
        onItemClick = { slug -> historyViewModel.onFilmClicked(slug) },
        onLongClick = { slug -> onLongClick(slug) },
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        (requireActivity() as AppBarProvider).setupAppBar(binding.appBar)
        binding.appBar.findViewById<ImageButton>(R.id.app_bar_search).visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        observeEffect()
    }

    override fun onResume() {
        super.onResume()
        historyViewModel.loadHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeState() {
        launchAndRepeatWithViewLifecycle {
                historyViewModel.state.collect { state ->
                    if (_binding == null) return@collect
                    if (state.films.isEmpty()) {
                        binding.text.visibility = View.VISIBLE
                    } else {
                        binding.text.visibility = View.GONE
                        binding.recyclerView.adapter = filmAdapter
                        filmAdapter.submitList(state.films)
                    }
                    if (state.errorMessage != null) {
                        binding.text.visibility = View.VISIBLE
                        binding.text.text = state.errorMessage
                    }
                }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                historyViewModel.effect.collect { effect ->
                    when (effect) {
                        is HistoryUiEffect.ShowToast -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is HistoryUiEffect.NavigateToFilm -> {
                            findNavController().navigate(NavRoutes.filmDetails(effect.slug))
                        }
                    }
                }
        }
    }

    private fun onLongClick(slug: String) {
        context?.let { ctx ->
            DeleteHistoryDialog.showDeleteFilmFromHistory(ctx, slug) {
                historyViewModel.deleteHistory(slug)
            }
        }
    }
}

