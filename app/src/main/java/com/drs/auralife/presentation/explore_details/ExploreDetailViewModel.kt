package com.drs.auralife.presentation.explore_details

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

    fun getFilmsByCategory(slug: String, page: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val result = getFilmsByCategoryUseCase(slug, page)
                _state.value = ExploreDetailUiState(
                    films = result.data,
                    totalPages = result.totalPages,
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun loadMoreFilmsByCategory(slug: String, page: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingMore = true)
            try {
                val result = getFilmsByCategoryUseCase(slug, page)
                val appended = _state.value.films + result.data
                _state.value = _state.value.copy(films = appended, totalPages = result.totalPages, isLoadingMore = false)
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
