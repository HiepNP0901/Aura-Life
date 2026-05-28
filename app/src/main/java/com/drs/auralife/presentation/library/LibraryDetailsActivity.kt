package com.drs.auralife.presentation.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.drs.auralife.presentation.viewmodel.FilmsViewModel
import com.drs.auralife.data.firebase.realtime.database.user.library.Library
import com.drs.auralife.data.firebase.realtime.database.user.library.LibraryRepository
import com.drs.auralife.databinding.ActivityLibraryDetailsBinding
import com.drs.auralife.domain.model.Film
import com.drs.auralife.presentation.film.FilmAdapter
import com.drs.auralife.presentation.film.HORIZONTAL

const val LIBRARY_NAME = "@libraryName"

@dagger.hilt.android.AndroidEntryPoint
class LibraryDetailsActivity :
    AppCompatActivity(),
    FilmAdapter.FragmentListener {
    private val binding by lazy { ActivityLibraryDetailsBinding.inflate(layoutInflater) }
    private val viewModel: FilmsViewModel by viewModels()
    private val filmAdapter = FilmAdapter(mutableListOf(), HORIZONTAL, false)
    private lateinit var library: Library

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.adapter = filmAdapter

        intent.getStringExtra(LIBRARY_NAME)?.let {
            @SuppressLint("SetTextI18n")
            binding.tvNameApp.text = "${binding.tvNameApp.text} - $it"
            LibraryRepository.getLibraryData(it) { library ->
                this.library = library
                val tempList = mutableListOf<Film>()
                for (slug in library.listFilm.map { it.slug }) {
                    viewModel.fetchFilmDetails(slug) { filmDetails: com.drs.auralife.domain.model.FilmDetails? ->
                        filmDetails?.let { fd ->
                            tempList.add(
                                Film(
                                    id = fd.slug,
                                    slug = fd.slug,
                                    title = fd.title,
                                    posterUrl = fd.posterUrl,
                                    thumbUrl = fd.thumbUrl,
                                    description = fd.description,
                                    category = fd.categories?.firstOrNull() ?: "",
                                    episodeCount = fd.episodeTotal?.toIntOrNull() ?: 0,
                                )
                            )

                            if (tempList.size == library.listFilm.size) {
                                val sortedList = library.listFilm.mapNotNull { filmLibrary ->
                                    tempList.find { it.slug == filmLibrary.slug }
                                }
                                filmAdapter.replaceItems(sortedList)
                            }
                        }
                    }
                }
            }
        }

        filmAdapter.setCallback(this)
    }

    override fun onRestart() {
        super.onRestart()
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            binding.root.isSelected = true
        }, 3000)
    }

    override fun onPause() {
        super.onPause()
        binding.root.isSelected = false
    }

    override fun onLongClick(slug: String) {
        library.let { library ->
            EditLibrary.showDeleteFilmFromLibrary(this, library.name, slug) {
                filmAdapter.removeItem(slug)
                val newSlug = library.listFilm.map { it.slug }.last { it != slug }
                viewModel.fetchFilmDetails(newSlug) { filmDetails: com.drs.auralife.domain.model.FilmDetails? ->
                    filmDetails?.let { fd ->
                        LibraryRepository.updatePosterUrl(library.name, fd.posterUrl)
                    }
                }
            }
        }
    }
}

