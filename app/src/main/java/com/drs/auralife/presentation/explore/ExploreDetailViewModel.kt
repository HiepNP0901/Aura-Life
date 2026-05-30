package com.drs.auralife.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.usecase.GetFilmsByCategoryUseCase
import com.drs.auralife.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryFilmsData(
    val films: List<Film>,
    val totalPages: Int,
)

@HiltViewModel
class ExploreDetailViewModel @Inject constructor(
    private val getFilmsByCategoryUseCase: GetFilmsByCategoryUseCase,
) : ViewModel() {

    private val _categoryFilmsState = MutableStateFlow<UiState<CategoryFilmsData>>(UiState.Loading)
    val categoryFilmsState: StateFlow<UiState<CategoryFilmsData>> = _categoryFilmsState.asStateFlow()

    fun getFilmsByCategory(slug: String, page: Int) {
        viewModelScope.launch {
            _categoryFilmsState.value = UiState.Loading
            try {
                val result = getFilmsByCategoryUseCase(slug, page)
                _categoryFilmsState.value = UiState.Success(CategoryFilmsData(result.data, result.totalPages))
            } catch (e: Exception) {
                _categoryFilmsState.value = UiState.Error(e.message ?: "Failed to load films")
            }
        }
    }

    fun loadMoreFilmsByCategory(slug: String, page: Int) {
        viewModelScope.launch {
            val current = _categoryFilmsState.value
            if (current is UiState.Success) {
                try {
                    val result = getFilmsByCategoryUseCase(slug, page)
                    val appended = current.data.films + result.data
                    _categoryFilmsState.value = UiState.Success(current.data.copy(films = appended))
                } catch (e: Exception) {
                    _categoryFilmsState.value = UiState.Error(e.message ?: "Failed to load more")
                }
            }
        }
    }
}
