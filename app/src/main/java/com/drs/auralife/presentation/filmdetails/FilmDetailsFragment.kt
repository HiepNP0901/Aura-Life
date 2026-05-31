package com.drs.auralife.presentation.filmdetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.presentation.common.launchAndRepeatWithViewLifecycle
import androidx.navigation.fragment.findNavController
import com.drs.auralife.R
import com.drs.auralife.presentation.common.MyAppGlideModule
import com.drs.auralife.presentation.navigation.NavRoutes
import com.drs.auralife.databinding.ActivityFilmDetailsBinding
import com.drs.auralife.presentation.library.AddToLibraryDialog
import com.drs.auralife.presentation.library.LibraryUiEffect
import com.drs.auralife.presentation.library.LibraryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilmDetailsFragment : Fragment() {

    private val filmDetailsViewModel: FilmDetailsViewModel by viewModels()
    private val libraryViewModel: LibraryViewModel by viewModels(ownerProducer = { requireActivity() })
    private var _binding: ActivityFilmDetailsBinding? = null
    private val binding get() = _binding ?: error("Binding accessed after onDestroyView")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ActivityFilmDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        observeEffect()
        observeOperationResult()
        val slug = requireArguments().getString("slug") ?: return
        filmDetailsViewModel.getFilmDetails(slug)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun observeState() {
        launchAndRepeatWithViewLifecycle {
                filmDetailsViewModel.state.collect { state ->
                    when {
                        state.isLoading -> binding.root.visibility = View.GONE
                        state.film != null -> {
                            binding.root.visibility = View.VISIBLE
                            updateUI(state.film)
                        }
                        state.errorMessage != null -> {
                            binding.root.visibility = View.GONE
                            Toast.makeText(context, state.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }

    private fun observeEffect() {
        launchAndRepeatWithViewLifecycle {
                filmDetailsViewModel.effect.collect { effect ->
                    when (effect) {
                        is FilmDetailsUiEffect.ShowToast -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        is FilmDetailsUiEffect.NavigateToPlayFilm -> {
                            findNavController().navigate(NavRoutes.playFilm(effect.slug))
                        }
                        is FilmDetailsUiEffect.NavigateToLogin -> {
                            findNavController().navigate(NavRoutes.LOGIN)
                        }
                    }
                }
        }
    }

    private fun updateUI(film: com.drs.auralife.domain.model.FilmDetails) {
        binding.nameFilm.text = film.title
        binding.nameEngFilm.text = film.originName
        binding.filmDescription.text = HtmlCompat.fromHtml(film.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

        for ((key, value) in getFilmDetailsMap(film)) {
            binding.root.findViewById<LinearLayout>(R.id.filmDetails).addView(createFilmDetailItem(key, value))
        }

        MyAppGlideModule.loadImage(requireContext(), film.posterUrl, binding.posterView)
        MyAppGlideModule.loadImage(requireContext(), film.thumbUrl, binding.thumbView)

        binding.trailerButton.setOnClickListener {
            film.trailerUrl?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        binding.playButton.setOnClickListener {
            filmDetailsViewModel.onPlayClicked(film.slug)
        }

        binding.addToLibrary.setOnClickListener {
            if (libraryViewModel.isLoggedIn()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    libraryViewModel.getLibraries()
                    AddToLibraryDialog.showAddLibraryDialog(
                        context = requireContext(),
                        slug = film.slug,
                        posterUrl = film.posterUrl,
                        episodeCurrent = film.episodeCurrent,
                        libraries = libraryViewModel.librariesState.value.libraries,
                        onAddToLibrary = { libraryName ->
                            libraryViewModel.addToLibrary(libraryName, film.slug, film.posterUrl, film.episodeCurrent ?: "")
                        },
                        onCreateLibrary = { newName ->
                            libraryViewModel.createLibrary(newName, film.posterUrl, film.slug, film.episodeCurrent ?: "")
                        },
                    )
                }
            } else {
                filmDetailsViewModel.onLoginNeeded()
            }
        }
    }

    private fun observeOperationResult() {
        launchAndRepeatWithViewLifecycle {
                libraryViewModel.effect.collect { effect ->
                    when (effect) {
                        is LibraryUiEffect.ShowToast -> {
                            Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
        }
    }

    private fun getFilmDetailsMap(film: com.drs.auralife.domain.model.FilmDetails): Map<String, String?> = mapOf(
        getString(R.string.status) to film.episodeCurrent,
        getString(R.string.episode_number) to film.episodeTotal,
        getString(R.string.duration) to film.duration,
        getString(R.string.quality) to film.quality,
        getString(R.string.language) to film.language,
        getString(R.string.director) to film.directors?.joinToString(", "),
        getString(R.string.casts) to film.actors?.joinToString(", "),
        getString(R.string.category) to film.categories?.joinToString(", "),
        getString(R.string.year_of_release) to film.year?.toString(),
        getString(R.string.country) to film.countries?.joinToString(", "),
    )

    private fun createFilmDetailItem(key: String, value: String?): View {
        val itemDetail = layoutInflater.inflate(R.layout.item_detail, LinearLayout(requireContext()))
        itemDetail.findViewById<TextView>(R.id.nameDetail).text = key
        itemDetail.findViewById<TextView>(R.id.valueDetail).text = value ?: "N/A"
        return itemDetail
    }
}

