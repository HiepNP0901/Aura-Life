package com.drs.auralife.feature.library.library_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.repository.LibraryRepository
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
import com.drs.auralife.domain.usecase.GetLibraryUseCase
import com.drs.auralife.domain.usecase.RemoveFilmFromLibraryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryDetailsViewModel @Inject constructor(
    private val getLibraryUseCase: GetLibraryUseCase,
    private val getFilmDetailsUseCase: GetFilmDetailsUseCase,
    private val removeFilmFromLibraryUseCase: RemoveFilmFromLibraryUseCase,
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryDetailUiState())
    val state: StateFlow<LibraryDetailUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LibraryDetailUiEffect>()
    val effect: SharedFlow<LibraryDetailUiEffect> = _effect.asSharedFlow()

    fun loadLibraryFilms(name: String) {
        viewModelScope.launch {
            _state.value = LibraryDetailUiState()
            try {
                val lib = getLibraryUseCase().find { it.name == name } ?: return@launch
                val films = buildFilmsFromLibrary(lib)
                _state.value = LibraryDetailUiState(films = films)
            } catch (e: Exception) {
                _state.value = LibraryDetailUiState(errorMessage = e.message)
            }
        }
    }

    private suspend fun buildFilmsFromLibrary(lib: Library): List<Film> {
        val slugs = lib.films.map { it.slug }
        val detailsMap = getFilmDetailsUseCase.batch(slugs)
        return lib.films.mapNotNull { filmLib ->
            detailsMap[filmLib.slug]?.let { fd ->
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
            }
        }
    }

    fun removeFilm(libraryName: String, slug: String) {
        viewModelScope.launch {
            try {
                removeFilmFromLibraryUseCase(libraryName, slug)

                val lib = getLibraryUseCase().find { it.name == libraryName }
                if (lib != null && lib.films.isNotEmpty()) {
                    val slugs = lib.films.map { it.slug }
                    val detailsMap = getFilmDetailsUseCase.batch(slugs)
                    val firstFilmDetail = detailsMap[slugs.first()]
                    if (firstFilmDetail != null) {
                        libraryRepository.updatePosterUrl(libraryName, firstFilmDetail.posterUrl)
                    }
                }

                loadLibraryFilms(libraryName)
                _effect.emit(LibraryDetailUiEffect.ShowToast("Removed from library"))
            } catch (e: Exception) {
                _effect.emit(LibraryDetailUiEffect.ShowToast(e.message ?: "Remove failed"))
            }
        }
    }

    fun onFilmClicked(slug: String) {
        viewModelScope.launch {
            _effect.emit(LibraryDetailUiEffect.NavigateToFilm(slug))
        }
    }
}
