package com.drs.auralife.ui.library

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.drs.auralife.data.FilmViewModelFactory
import com.drs.auralife.data.FilmsViewModel
import com.drs.auralife.data.firebase.library.Library
import com.drs.auralife.data.firebase.library.LibraryRepository
import com.drs.auralife.databinding.ActivityLibraryBinding
import com.drs.auralife.ui.film.FilmAdapter
import com.drs.auralife.ui.film.LINEAR

const val LIBRARY_NAME = "@libraryName"

class LibraryActivity : AppCompatActivity(), FilmAdapter.FragmentListener {
    private val binding by lazy { ActivityLibraryBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        ViewModelProvider(
            this, FilmViewModelFactory(this)
        )[FilmsViewModel::class.java]
    }
    private val adapter by lazy { FilmAdapter(mutableListOf(), LINEAR) }
    private lateinit var library: Library

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter

        intent.getStringExtra(LIBRARY_NAME)?.let {
            LibraryRepository().getLibraryData(it) { library ->
                this.library = library
                for (slug in library.listSlug) {
                    viewModel.fetchFilmDetails(slug) {
                        it?.let {
                            adapter.addItem(listOf(it.movie))
                        }
                    }
                }
            }
        }

        adapter.setCallback(this)
    }

    override fun onLongClick(slug: String) {
        library.let {library ->
            EditLibrary.showDeleteFilmFromLibrary(this, library.name, slug) {
                adapter.removeItem(slug)
                val newSlug = library.listSlug.filter { it != slug }.last()
                viewModel.fetchFilmDetails(newSlug) {
                    it?.let {
                        LibraryRepository().updatePosterUrl(library.name, it.movie.posterUrl)
                    }
                }
            }
        }
    }
}