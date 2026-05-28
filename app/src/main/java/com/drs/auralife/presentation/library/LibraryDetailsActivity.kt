package com.drs.auralife.presentation.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.drs.auralife.presentation.viewmodel.FilmsViewModel
import com.drs.auralife.data.firebase.realtime.database.user.library.Library
import com.drs.auralife.data.firebase.realtime.database.user.library.LibraryRepository
import com.drs.auralife.data.model.film.Movie
import com.drs.auralife.databinding.ActivityLibraryDetailsBinding
import com.drs.auralife.presentation.film.FilmAdapter
import com.drs.auralife.presentation.film.HORIZONTAL

const val LIBRARY_NAME = "@libraryName"

class LibraryDetailsActivity :
    AppCompatActivity(),
    FilmAdapter.FragmentListener {
    private val binding by lazy { ActivityLibraryDetailsBinding.inflate(layoutInflater) }
    private val viewModel by lazy { FilmsViewModel(this) }
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
                val tempList = mutableListOf<Movie>()
                for (slug in library.listFilm.map { it.slug }) {
                    viewModel.fetchFilmDetailsLegacy(slug) { filmDetails: com.drs.auralife.data.model.film.FilmDetails? ->
                        filmDetails?.let {
                            tempList.add(it.movie)

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
                viewModel.fetchFilmDetailsLegacy(newSlug) { filmDetails: com.drs.auralife.data.model.film.FilmDetails? ->
                    filmDetails?.let {
                        LibraryRepository.updatePosterUrl(library.name, it.movie.posterUrl.toString())
                    }
                }
            }
        }
    }
}

