package com.drs.auralife.presentation.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.usecase.GetCategoriesUseCase
import com.drs.auralife.domain.usecase.GetFilmsByCategoryUseCase
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

    private val _categoriesState = MutableStateFlow<List<Category>>(emptyList())
    val categoriesState: StateFlow<List<Category>> = _categoriesState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _categoriesState.value = getCategoriesUseCase()
            } catch (e: Exception) {
                Log.e("ExploreViewModel", "loadCategories failed", e)
            } finally {
                _isLoading.value = false
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
