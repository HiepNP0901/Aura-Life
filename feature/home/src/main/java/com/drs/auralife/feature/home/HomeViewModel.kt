package com.drs.auralife.feature.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.usecase.GetBannersUseCase
import com.drs.auralife.domain.usecase.GetLatestFilmsUseCase
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
class HomeViewModel @Inject constructor(
    private val getBannersUseCase: GetBannersUseCase,
    private val getLatestFilmsUseCase: GetLatestFilmsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeUiEffect>()
    val effect: SharedFlow<HomeUiEffect> = _effect.asSharedFlow()

    fun loadBanners() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingBanners = true)
            try {
                val banners = getBannersUseCase()
                _state.value = _state.value.copy(banners = banners, isLoadingBanners = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoadingBanners = false)
                _effect.emit(HomeUiEffect.ShowToast(e.message ?: "Failed to load banners"))
            }
        }
    }

    fun loadLatestFilms() {
        val page = 1
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingFilms = true, errorMessage = null)
            try {
                val result = getLatestFilmsUseCase(page)
                _state.value = _state.value.copy(
                    films = result.data,
                    totalPages = result.totalPages,
                    currentPage = page,
                    isLoadingFilms = false,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoadingFilms = false, errorMessage = e.message)
                _effect.emit(HomeUiEffect.ShowToast(e.message ?: "Failed to load films"))
            }
        }
    }

    fun onScrolledToBottom() {
        val current = _state.value
        if (current.isLoadingMore || current.currentPage >= current.totalPages) return
        val nextPage = current.currentPage + 1
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)
            try {
                val result = getLatestFilmsUseCase(nextPage)
                val allFilms = _state.value.films + result.data
                _state.value = _state.value.copy(
                    films = allFilms,
                    totalPages = result.totalPages,
                    currentPage = nextPage,
                    isLoadingMore = false,
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "loadMoreLatestFilms failed", e)
                _state.value = _state.value.copy(isLoadingMore = false)
                _effect.emit(HomeUiEffect.ShowToast(e.message ?: "Failed to load more films"))
            }
        }
    }

    fun onFilmClicked(slug: String) {
        viewModelScope.launch {
            _effect.emit(HomeUiEffect.NavigateToFilm(slug))
        }
    }

    fun checkConnectivity(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        val connected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        _state.value = _state.value.copy(isConnected = connected)
        return connected
    }
}
