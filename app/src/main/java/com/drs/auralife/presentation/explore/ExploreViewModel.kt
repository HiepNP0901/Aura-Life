package com.drs.auralife.presentation.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.usecase.GetCategoriesUseCase
import com.drs.auralife.domain.usecase.GetFilmsByCategoryUseCase
import com.drs.auralife.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getFilmsByCategoryUseCase: GetFilmsByCategoryUseCase,
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<UiState<List<Category>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<Category>>> = _categoriesState.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            try {
                _categoriesState.value = UiState.Success(getCategoriesUseCase())
            } catch (e: Exception) {
                Log.e("ExploreViewModel", "loadCategories failed", e)
                _categoriesState.value = UiState.Error(e.message ?: "Failed to load categories")
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
}
