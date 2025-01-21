package com.drs.auralife.ui.library

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.firebase.library.EditLibrary
import com.drs.auralife.data.firebase.library.Library
import com.drs.auralife.data.firebase.library.LibraryRepository
import com.drs.auralife.data.model.film.Movie
import com.drs.auralife.databinding.ActivityLibraryDetailsBinding
import com.drs.auralife.ui.film.FilmAdapter
import com.drs.auralife.ui.film.HORIZONTAL

const val LIBRARY_NAME = "@libraryName"

class LibraryDetailsActivity : AppCompatActivity(), FilmAdapter.FragmentListener {
    private val binding by lazy { ActivityLibraryDetailsBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ViewModelProvider(
            this, FilmViewModelFactory(this)
        )[FilmsViewModel::class.java]
    }
    private val filmAdapter by lazy { FilmAdapter(mutableListOf(), HORIZONTAL, false) }
    private lateinit var library: Library

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.adapter = filmAdapter

        intent.getStringExtra(LIBRARY_NAME)?.let {
            LibraryRepository.getLibraryData(it) { library ->
                this.library = library
                val tempList = mutableListOf<Movie>()
                for (slug in library.listFilm.map { it.slug }) {
                    viewModel.fetchFilmDetails(slug) {
                        it?.let {
                            tempList.add(it.movie)

                            if (tempList.size == library.listFilm.size) {
                                val sortedList = library.listFilm.mapNotNull { h ->
                                    tempList.find { it.slug == h.slug }
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
        library.let {library ->
            EditLibrary.showDeleteFilmFromLibrary(this, library.name, slug) {
                filmAdapter.removeItem(slug)
                val newSlug = library.listFilm.map { it.slug }.filter { it != slug }.last()
                viewModel.fetchFilmDetails(newSlug) {
                    it?.let {
                        LibraryRepository.updatePosterUrl(library.name, it.movie.posterUrl)
                    }
                }
            }
        }
    }
}