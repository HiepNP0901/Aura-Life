package com.drs.auralife.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.model.LibraryFilm
import com.drs.auralife.domain.usecase.AddToLibraryUseCase
import com.drs.auralife.domain.usecase.CreateLibraryUseCase
import com.drs.auralife.domain.usecase.DeleteLibraryUseCase
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
import com.drs.auralife.domain.usecase.GetLibraryUseCase
import com.drs.auralife.domain.usecase.RemoveFilmFromLibraryUseCase
import com.drs.auralife.domain.usecase.RenameLibraryUseCase
import com.drs.auralife.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getLibraryUseCase: GetLibraryUseCase,
    private val addToLibraryUseCase: AddToLibraryUseCase,
    private val createLibraryUseCase: CreateLibraryUseCase,
    private val removeFilmFromLibraryUseCase: RemoveFilmFromLibraryUseCase,
    private val renameLibraryUseCase: RenameLibraryUseCase,
    private val deleteLibraryUseCase: DeleteLibraryUseCase,
    private val getFilmDetailsUseCase: GetFilmDetailsUseCase,
) : ViewModel() {

    private val _librariesState = MutableStateFlow<UiState<List<Library>>>(UiState.Loading)
    val librariesState: StateFlow<UiState<List<Library>>> = _librariesState.asStateFlow()

    private val _libraryFilmsState = MutableStateFlow<UiState<List<Film>>>(UiState.Loading)
    val libraryFilmsState: StateFlow<UiState<List<Film>>> = _libraryFilmsState.asStateFlow()

    private val _operationResult = MutableSharedFlow<Result<Boolean>>()
    val operationResult: SharedFlow<Result<Boolean>> = _operationResult.asSharedFlow()

    private val _librariesLoaded = MutableStateFlow<List<Library>>(emptyList())
    val librariesLoaded: StateFlow<List<Library>> = _librariesLoaded.asStateFlow()

    fun getLibraries() {
        viewModelScope.launch {
            _librariesState.value = UiState.Loading
            _librariesState.value = try {
                val libs = getLibraryUseCase()
                _librariesLoaded.emit(libs)
                UiState.Success(libs)
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Failed to load libraries")
            }
        }
    }

    fun loadLibraryFilms(name: String) {
        viewModelScope.launch {
            _libraryFilmsState.value = UiState.Loading
            _libraryFilmsState.value = try {
                val lib = getLibraryUseCase().find { it.name == name } ?: return@launch
                val films = buildFilmsFromLibrary(lib)
                UiState.Success(films)
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Failed to load library films")
            }
        }
    }

    private suspend fun buildFilmsFromLibrary(lib: Library): List<Film> {
        return coroutineScope {
            lib.films.map { filmLib ->
                async {
                    val details = getFilmDetailsUseCase(filmLib.slug)
                    details?.let {
                        Film(
                            id = it.slug,
                            slug = it.slug,
                            title = it.title,
                            posterUrl = it.posterUrl,
                            thumbUrl = it.thumbUrl,
                            description = it.description,
                            category = it.categories?.firstOrNull() ?: "",
                            episodeCount = it.episodeTotal?.toIntOrNull() ?: 0,
                        )
                    }
                }
            }.mapNotNull { it.await() }
                .let { sorted ->
                    lib.films.mapNotNull { filmLib -> sorted.find { it.slug == filmLib.slug } }
                }
        }
    }

    fun addToLibrary(
        name: String,
        slug: String,
        posterUrl: String,
        episodeCurrent: String,
    ) {
        viewModelScope.launch {
            val library = Library(
                name = name,
                posterUrl = posterUrl,
                films = listOf(
                    LibraryFilm(
                        slug = slug,
                        currentEpisode = episodeCurrent,
                    )
                ),
            )
            val result = addToLibraryUseCase(library)
            _operationResult.emit(Result.success(result))
        }
    }

    fun createLibrary(
        name: String,
        posterUrl: String,
        slug: String,
        episodeCurrent: String,
    ) {
        viewModelScope.launch {
            val library = Library(
                name = name,
                posterUrl = posterUrl,
                films = listOf(
                    LibraryFilm(
                        slug = slug,
                        currentEpisode = episodeCurrent,
                    )
                ),
            )
            val result = createLibraryUseCase(library)
            _operationResult.emit(Result.success(result))
        }
    }

    fun removeFilm(libraryName: String, slug: String) {
        viewModelScope.launch {
            try {
                val result = removeFilmFromLibraryUseCase(libraryName, slug)
                _operationResult.emit(Result.success(result))
            } catch (e: Exception) {
                _operationResult.emit(Result.failure(e))
            }
        }
    }

    fun renameLibrary(oldName: String, newName: String) {
        viewModelScope.launch {
            try {
                val result = renameLibraryUseCase(oldName, newName)
                _operationResult.emit(Result.success(result))
            } catch (e: Exception) {
                _operationResult.emit(Result.failure(e))
            }
        }
    }

    fun deleteLibrary(name: String) {
        viewModelScope.launch {
            try {
                val result = deleteLibraryUseCase(name)
                _operationResult.emit(Result.success(result))
            } catch (e: Exception) {
                _operationResult.emit(Result.failure(e))
            }
        }
    }
}
