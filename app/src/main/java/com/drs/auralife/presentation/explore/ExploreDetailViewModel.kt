package com.drs.auralife.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.usecase.GetFilmsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreDetailViewModel @Inject constructor(
    private val getFilmsByCategoryUseCase: GetFilmsByCategoryUseCase,
) : ViewModel() {

    private val _categoryFilmsState = MutableStateFlow<List<Film>>(emptyList())
    val categoryFilmsState: StateFlow<List<Film>> = _categoryFilmsState.asStateFlow()

    private val _categoryTotalPages = MutableStateFlow(0)
    val categoryTotalPages: StateFlow<Int> = _categoryTotalPages.asStateFlow()

    private val _isLoadingState = MutableStateFlow(false)
    val isLoadingState: StateFlow<Boolean> = _isLoadingState.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState.asStateFlow()

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
}
