package com.drs.auralife.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Library
import com.drs.auralife.domain.model.LibraryFilm
import com.drs.auralife.domain.repository.AuthRepository
import com.drs.auralife.domain.usecase.AddToLibraryUseCase
import com.drs.auralife.domain.usecase.CreateLibraryUseCase
import com.drs.auralife.domain.usecase.DeleteLibraryUseCase
import com.drs.auralife.domain.usecase.GetLibraryUseCase
import com.drs.auralife.domain.usecase.RemoveFilmFromLibraryUseCase
import com.drs.auralife.domain.usecase.RenameLibraryUseCase
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
class LibraryViewModel @Inject constructor(
    private val getLibraryUseCase: GetLibraryUseCase,
    private val addToLibraryUseCase: AddToLibraryUseCase,
    private val createLibraryUseCase: CreateLibraryUseCase,
    private val removeFilmFromLibraryUseCase: RemoveFilmFromLibraryUseCase,
    private val renameLibraryUseCase: RenameLibraryUseCase,
    private val deleteLibraryUseCase: DeleteLibraryUseCase,
    private val authRepository: AuthRepository,
) : ViewModel() {

    fun isLoggedIn() = authRepository.isLoggedIn()

    private val _librariesState = MutableStateFlow(LibraryUiState())
    val librariesState: StateFlow<LibraryUiState> = _librariesState.asStateFlow()

    private val _operationResult = MutableSharedFlow<Result<Boolean>>()
    val operationResult: SharedFlow<Result<Boolean>> = _operationResult.asSharedFlow()

    private val _librariesLoaded = MutableStateFlow<List<Library>>(emptyList())
    val librariesLoaded: StateFlow<List<Library>> = _librariesLoaded.asStateFlow()

    fun getLibraries() {
        viewModelScope.launch {
            _librariesState.value = _librariesState.value.copy(isLoading = true)
            try {
                val libs = getLibraryUseCase()
                _librariesLoaded.emit(libs)
                _librariesState.value = _librariesState.value.copy(libraries = libs, isLoading = false)
            } catch (e: Exception) {
                _librariesState.value = _librariesState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun addToLibrary(name: String, slug: String, posterUrl: String, episodeCurrent: String) {
        viewModelScope.launch {
            val library = Library(
                name = name,
                posterUrl = posterUrl,
                films = listOf(LibraryFilm(slug = slug, currentEpisode = episodeCurrent)),
            )
            val result = addToLibraryUseCase(library)
            _operationResult.emit(Result.success(result))
        }
    }

    fun createLibrary(name: String, posterUrl: String, slug: String, episodeCurrent: String) {
        viewModelScope.launch {
            val library = Library(
                name = name,
                posterUrl = posterUrl,
                films = listOf(LibraryFilm(slug = slug, currentEpisode = episodeCurrent)),
            )
            val result = createLibraryUseCase(library)
            _operationResult.emit(Result.success(result))
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
