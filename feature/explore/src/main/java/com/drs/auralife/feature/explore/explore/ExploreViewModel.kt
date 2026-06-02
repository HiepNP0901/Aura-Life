package com.drs.auralife.feature.explore.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.result.errorMessage
import com.drs.auralife.domain.usecase.GetCategoriesUseCase
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
class ExploreViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getFilmsByCategoryUseCase: GetFilmsByCategoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ExploreUiState())
    val state: StateFlow<ExploreUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ExploreUiEffect>()
    val effect: SharedFlow<ExploreUiEffect> = _effect.asSharedFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = getCategoriesUseCase()) {
                is Result.Success -> {
                    _state.value = _state.value.copy(categories = result.data, isLoading = false)
                    result.data.forEach { category ->
                        loadFilmsForCategory(category.slug)
                    }
                }

                is Result.Error -> {
                    Log.e("ExploreViewModel", "loadCategories failed", result.exception)
                    _state.value = _state.value.copy(isLoading = false, errorMessage = result.errorMessage)
                }

                is Result.Loading -> {}
            }
        }
    }

    private suspend fun loadFilmsForCategory(slug: String) {
        when (val result = getFilmsByCategoryUseCase(slug, 1)) {
            is Result.Success -> {
                val current = _state.value.filmsByCategory.toMutableMap()
                current[slug] = result.data.data
                _state.value = _state.value.copy(filmsByCategory = current)
            }

            is Result.Error -> {
                Log.e("ExploreViewModel", "loadFilmsForCategory failed for $slug", result.exception)
                _effect.emit(ExploreUiEffect.ShowToast("Failed to load films for category"))
            }

            is Result.Loading -> {}
        }
    }

    fun onCategoryClicked(slug: String, name: String) {
        viewModelScope.launch {
            _effect.emit(ExploreUiEffect.NavigateToCategory(slug, name))
        }
    }

    fun onFilmClicked(slug: String) {
        viewModelScope.launch {
            _effect.emit(ExploreUiEffect.NavigateToFilm(slug))
        }
    }
}

