package com.drs.auralife.feature.library.library_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drs.auralife.designsystem.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.navigation.NavRoutes
import com.drs.auralife.feature.library.databinding.FragmentLibraryDetailsBinding
import com.drs.auralife.feature.library.library.EditLibraryDialog
import com.drs.auralife.feature.library.library_details.adapter.LibraryFilmAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryDetailsFragment : Fragment() {

    private val libraryDetailsViewModel: LibraryDetailsViewModel by viewModels()
    private var _binding: FragmentLibraryDetailsBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")
    private var libraryName: String = ""

    private val filmAdapter = LibraryFilmAdapter(
        onItemClick = { slug -> libraryDetailsViewModel.onFilmClicked(slug) },
        onLongClick = { slug -> onLongClick(slug) },
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLibraryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = filmAdapter
        libraryName = requireArguments().getString("name") ?: return

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvCategoryName.text = libraryName

        observeLibraryFilms()
        observeEffect()
        libraryDetailsViewModel.loadLibraryFilms(libraryName)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun observeLibraryFilms() {
        launchAndRepeatWithViewLifecycle {
                libraryDetailsViewModel.state.collect { state ->
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
                libraryDetailsViewModel.effect.collect { effect ->
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
        EditLibraryDialog.showDeleteFilmFromLibrary(requireContext()) {
            libraryDetailsViewModel.removeFilm(libraryName, slug)
        }
    }
}

