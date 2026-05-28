package com.drs.auralife.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.FilmDetails
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
import com.drs.auralife.domain.usecase.GetFilmsByCategoryUseCase
import com.drs.auralife.domain.usecase.GetLatestFilmsUseCase
import com.drs.auralife.domain.usecase.SearchFilmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import javax.inject.Inject

@HiltViewModel
class FilmsViewModel @Inject constructor(
    private val getLatestFilmsUseCase: GetLatestFilmsUseCase,
    private val getFilmsByCategoryUseCase: GetFilmsByCategoryUseCase,
    private val searchFilmsUseCase: SearchFilmsUseCase,
    private val getFilmDetailsUseCase: GetFilmDetailsUseCase,
) : ViewModel() {

    // StateFlow for reactive state management
    private val _latestFilmsState = MutableStateFlow<List<Film>>(emptyList())
    val latestFilmsState: StateFlow<List<Film>> = _latestFilmsState.asStateFlow()

    private val _latestFilmsTotalPages = MutableStateFlow(0)
    val latestFilmsTotalPages: StateFlow<Int> = _latestFilmsTotalPages.asStateFlow()

    private val _categoryFilmsState = MutableStateFlow<List<Film>>(emptyList())
    val categoryFilmsState: StateFlow<List<Film>> = _categoryFilmsState.asStateFlow()

    private val _categoryTotalPages = MutableStateFlow(0)
    val categoryTotalPages: StateFlow<Int> = _categoryTotalPages.asStateFlow()

    private val _searchResultsState = MutableStateFlow<List<Film>>(emptyList())
    val searchResultsState: StateFlow<List<Film>> = _searchResultsState.asStateFlow()

    private val _filmDetailsState = MutableStateFlow<FilmDetails?>(null)
    val filmDetailsState: StateFlow<FilmDetails?> = _filmDetailsState.asStateFlow()

    private val _isLoadingState = MutableStateFlow(false)
    val isLoadingState: StateFlow<Boolean> = _isLoadingState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

    fun getLatestFilms(page: Int) {
        viewModelScope.launch {
            try {
                _isLoadingState.value = true
                _errorState.value = null
                val result = getLatestFilmsUseCase(page)
                _latestFilmsState.value = result.data
                _latestFilmsTotalPages.value = result.totalPages
            } catch (e: Exception) {
                _errorState.value = e.message
            } finally {
                _isLoadingState.value = false
            }
        }
    }

    fun loadMoreLatestFilms(page: Int) {
        viewModelScope.launch {
            try {
                val result = getLatestFilmsUseCase(page)
                _latestFilmsState.value = _latestFilmsState.value + result.data
            } catch (e: Exception) {
                _errorState.value = e.message
            }
        }
    }

    fun getFilmsByCategory(slug: String, page: Int) {
        viewModelScope.launch {
            try {
                _isLoadingState.value = true
                _errorState.value = null
                val result = getFilmsByCategoryUseCase(slug, page)
                _categoryFilmsState.value = result.data
                _categoryTotalPages.value = result.totalPages
            } catch (e: Exception) {
                _errorState.value = e.message
            } finally {
                _isLoadingState.value = false
            }
        }
    }

    fun loadMoreFilmsByCategory(slug: String, page: Int) {
        viewModelScope.launch {
            try {
                val result = getFilmsByCategoryUseCase(slug, page)
                _categoryFilmsState.value = _categoryFilmsState.value + result.data
            } catch (e: Exception) {
                _errorState.value = e.message
            }
        }
    }

    fun searchFilms(keyword: String, limit: Int) {
        viewModelScope.launch {
            try {
                _isLoadingState.value = true
                _errorState.value = null
                val films = searchFilmsUseCase(keyword, limit)
                _searchResultsState.value = films
            } catch (e: Exception) {
                _errorState.value = e.message
            } finally {
                _isLoadingState.value = false
            }
        }
    }

    fun getFilmDetails(slug: String) {
        viewModelScope.launch {
            try {
                _isLoadingState.value = true
                _errorState.value = null
                val details = getFilmDetailsUseCase(slug)
                _filmDetailsState.value = details
            } catch (e: Exception) {
                _errorState.value = e.message
            } finally {
                _isLoadingState.value = false
            }
        }
    }

    fun fetchLatestFilms(
        page: Int,
        callback: (List<Film>?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(getLatestFilmsUseCase(page).data)
        }
    }

    fun fetchFilmsByCategory(
        slug: String,
        page: Int,
        callback: (List<Film>?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(getFilmsByCategoryUseCase(slug, page).data)
        }
    }

    fun searchFilms(
        keyword: String,
        limit: Int,
        callback: (List<Film>?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(searchFilmsUseCase(keyword, limit))
        }
    }

    fun fetchFilmDetails(
        slug: String,
        callback: (FilmDetails?) -> Unit,
    ) {
        viewModelScope.launch {
            Log.d("TAG", this.coroutineContext.toString())
            callback(getFilmDetailsUseCase(slug))
        }
    }
}
