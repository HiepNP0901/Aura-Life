package com.drs.auralife.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.drs.auralife.domain.usecase.GetBannersUseCase
import com.drs.auralife.domain.usecase.GetLatestFilmsUseCase
import com.drs.auralife.presentation.common.UiState
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

    private val _bannersState = MutableStateFlow<UiState<List<Pair<String, String>>>>(UiState.Loading)
    val bannersState: StateFlow<UiState<List<Pair<String, String>>>> = _bannersState.asStateFlow()

    private val _latestFilmsState = MutableStateFlow<UiState<HomeFilmsData>>(UiState.Loading)
    val latestFilmsState: StateFlow<UiState<HomeFilmsData>> = _latestFilmsState.asStateFlow()

    fun loadBanners() {
        viewModelScope.launch {
            _bannersState.value = UiState.Loading
            _bannersState.value = try {
                UiState.Success(getBannersUseCase())
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Failed to load banners")
            }
        }
    }

    fun getLatestFilms(page: Int) {
        viewModelScope.launch {
            _latestFilmsState.value = UiState.Loading
            _latestFilmsState.value = try {
                val result = getLatestFilmsUseCase(page)
                UiState.Success(HomeFilmsData(result.data, result.totalPages))
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Failed to load films")
            }
        }
    }

    fun loadMoreLatestFilms(page: Int) {
        viewModelScope.launch {
            val current = _latestFilmsState.value
            if (current !is UiState.Success) return@launch
            try {
                val result = getLatestFilmsUseCase(page)
                val allFilms = current.data.films + result.data
                _latestFilmsState.value = UiState.Success(HomeFilmsData(allFilms, result.totalPages))
            } catch (e: Exception) {
                Log.e("HomeViewModel", "loadMoreLatestFilms failed", e)
                _latestFilmsState.value = UiState.Error(e.message ?: "Failed to load more films")
            }
        }
    }
}
