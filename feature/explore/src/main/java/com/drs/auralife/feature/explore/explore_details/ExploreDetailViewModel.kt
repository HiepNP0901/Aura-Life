package com.drs.auralife.feature.explore.explore_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.usecase.GetFilmsByCategoryUseCase
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
class ExploreDetailViewModel @Inject constructor(
    private val getFilmsByCategoryUseCase: GetFilmsByCategoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreDetailUiState())
    val state: StateFlow<ExploreDetailUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ExploreDetailUiEffect>()
    val effect: SharedFlow<ExploreDetailUiEffect> = _effect.asSharedFlow()

    fun getFilmsByCategory(slug: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, currentPage = 1)
            try {
                val result = getFilmsByCategoryUseCase(slug, 1)
                _state.value = ExploreDetailUiState(
                    films = result.data,
                    totalPages = result.totalPages,
                    currentPage = 1,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun onScrolledToBottom(slug: String) {
        val current = _state.value
        if (current.isLoadingMore || current.currentPage >= current.totalPages) return
        val nextPage = current.currentPage + 1
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)
            try {
                val result = getFilmsByCategoryUseCase(slug, nextPage)
                val appended = _state.value.films + result.data
                _state.value = _state.value.copy(films = appended, totalPages = result.totalPages, currentPage = nextPage, isLoadingMore = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoadingMore = false, errorMessage = e.message)
            }
        }
    }

    fun onFilmClicked(slug: String) {
        viewModelScope.launch {
            _effect.emit(ExploreDetailUiEffect.NavigateToFilm(slug))
        }
    }
}
