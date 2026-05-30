package com.drs.auralife.presentation.library

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.drs.auralife.R
import com.drs.auralife.databinding.ActivityLibraryDetailsBinding
import com.drs.auralife.presentation.common.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

const val LIBRARY_NAME = "@libraryName"

@AndroidEntryPoint
class LibraryDetailsActivity :
    AppCompatActivity(),
    LibraryFilmAdapter.Listener {
    private val binding by lazy { ActivityLibraryDetailsBinding.inflate(layoutInflater) }
    private val libraryViewModel: LibraryViewModel by viewModels()
    private val filmAdapter = LibraryFilmAdapter()
    private var libraryName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.adapter = filmAdapter

        libraryName = intent.getStringExtra(LIBRARY_NAME)
        libraryName?.let {
            binding.tvNameApp.text = "${binding.tvNameApp.text} - $it"
            observeLibraryFilms()
            libraryViewModel.loadLibraryFilms(it)
        }

        filmAdapter.setCallback(this)
    }

    private fun observeLibraryFilms() {
        lifecycleScope.launch {
            libraryViewModel.libraryFilmsState.collect { state ->
                when (state) {
                    is UiState.Success -> {
                        filmAdapter.replaceItems(state.data)
                    }
                    is UiState.Error -> {
                        Toast.makeText(this@LibraryDetailsActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is UiState.Loading -> {}
                }
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onLongClick(slug: String) {
        libraryName?.let { name ->
            EditLibraryDialog.showDeleteFilmFromLibrary(this, name, slug) {
                filmAdapter.removeItem(slug)
                libraryViewModel.removeFilm(name, slug)
                libraryViewModel.loadLibraryFilms(name)
            }
        }
    }
}
