package com.drs.auralife.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.usecase.GetBannersUseCase
import com.drs.auralife.domain.usecase.GetLatestFilmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBannersUseCase: GetBannersUseCase,
    private val getLatestFilmsUseCase: GetLatestFilmsUseCase,
) : ViewModel() {

    private val _bannersState = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val bannersState: StateFlow<List<Pair<String, String>>> = _bannersState.asStateFlow()

    private val _latestFilmsState = MutableStateFlow<List<Film>>(emptyList())
    val latestFilmsState: StateFlow<List<Film>> = _latestFilmsState.asStateFlow()

    private val _latestFilmsTotalPages = MutableStateFlow(0)
    val latestFilmsTotalPages: StateFlow<Int> = _latestFilmsTotalPages.asStateFlow()

    fun loadBanners() {
        viewModelScope.launch {
            try {
                _bannersState.value = getBannersUseCase()
            } catch (_: Exception) {
            }
        }
    }

    fun getLatestFilms(page: Int) {
        viewModelScope.launch {
            try {
                val result = getLatestFilmsUseCase(page)
                _latestFilmsState.value = result.data
                _latestFilmsTotalPages.value = result.totalPages
            } catch (_: Exception) {
            }
        }
    }

    fun loadMoreLatestFilms(page: Int) {
        viewModelScope.launch {
            try {
                val result = getLatestFilmsUseCase(page)
                _latestFilmsState.value = _latestFilmsState.value + result.data
            } catch (_: Exception) {
            }
        }
    }
}
