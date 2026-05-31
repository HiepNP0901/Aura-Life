package com.drs.auralife.presentation.library_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.presentation.common.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.R
import com.drs.auralife.presentation.navigation.NavRoutes
import com.drs.auralife.databinding.ActivityLibraryDetailsBinding
import com.drs.auralife.presentation.library.EditLibraryDialog
import com.drs.auralife.presentation.library_details.adapter.LibraryFilmAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LibraryDetailsFragment : Fragment() {

    private val viewModel: LibraryDetailsViewModel by viewModels()
    private var _binding: ActivityLibraryDetailsBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")
    private var libraryName: String = ""

    private val filmAdapter = LibraryFilmAdapter(
        onItemClick = { slug -> viewModel.onFilmClicked(slug) },
        onLongClick = { slug -> onLongClick(slug) },
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityLibraryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = filmAdapter
        libraryName = requireArguments().getString("name") ?: return
        binding.tvNameApp.text = "${binding.tvNameApp.text} - $libraryName"

        observeLibraryFilms()
        observeEffect()
        viewModel.loadLibraryFilms(libraryName)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun observeLibraryFilms() {
        launchAndRepeatWithViewLifecycle {
                viewModel.state.collect { state ->
                    if (state.films.isNotEmpty()) {
                        filmAdapter.submitList(state.films)
                    }
                    if (state.errorMessage != null) {
                        Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                viewModel.effect.collect { effect ->
                    when (effect) {
                        is LibraryDetailUiEffect.ShowToast -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is LibraryDetailUiEffect.NavigateToFilm -> {
                            findNavController().navigate(NavRoutes.filmDetails(effect.slug))
                        }
                    }
                }
        }
    }

    private fun onLongClick(slug: String) {
        EditLibraryDialog.showDeleteFilmFromLibrary(requireContext(), libraryName, slug) {
            viewModel.removeFilm(libraryName, slug)
        }
    }
}

