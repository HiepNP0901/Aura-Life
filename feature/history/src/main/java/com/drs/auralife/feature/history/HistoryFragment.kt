package com.drs.auralife.feature.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.drs.auralife.core.navigation.AppNavigator
import com.drs.auralife.designsystem.AppBarProvider
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import com.drs.auralife.feature.history.adapter.HistoryFilmAdapter
import com.drs.auralife.feature.history.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import com.drs.auralife.core.designsystem.R as DsR

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private val appNavigator by lazy { AppNavigator(findNavController()) }

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
        binding.appBar.findViewById<ImageButton>(DsR.id.app_bar_search).visibility = View.GONE
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
                binding.loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE
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

                    is HistoryUiEffect.NavigateToFilmPlayer -> {
                        appNavigator.navigateToPlayFilm(effect.slug)
                    }
                }
            }
        }
    }

    private fun onLongClick(slug: String) {
        context?.let { ctx ->
            DeleteHistoryDialog.showDeleteFilmFromHistory(ctx) {
                historyViewModel.deleteHistory(slug)
            }
        }
    }
}

