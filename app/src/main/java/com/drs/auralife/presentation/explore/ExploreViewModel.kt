package com.drs.auralife.presentation.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.model.Film
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
            try {
                _state.value = _state.value.copy(categories = getCategoriesUseCase(), isLoading = false)
            } catch (e: Exception) {
                Log.e("ExploreViewModel", "loadCategories failed", e)
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun getFilmsByCategoryList(slug: String, page: Int, onResult: (List<Film>?) -> Unit) {
        viewModelScope.launch {
            try {
                val result = getFilmsByCategoryUseCase(slug, page)
                onResult(result.data)
            } catch (e: Exception) {
                Log.e("ExploreViewModel", "getFilmsByCategoryList failed", e)
                onResult(null)
            }
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
