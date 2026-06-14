package com.drs.auralife.feature.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.result.errorMessage
import com.drs.auralife.domain.usecase.AddToHistoryUseCase
import com.drs.auralife.domain.usecase.DeleteHistoryUseCase
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
import com.drs.auralife.domain.usecase.GetHistoryUseCase
import com.drs.auralife.feature.history.model.HistoryFilm
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
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase,
    private val getFilmDetailsUseCase: GetFilmDetailsUseCase,
    private val addToHistoryUseCase: AddToHistoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HistoryUiState())
    val state: StateFlow<HistoryUiState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HistoryUiEffect>()
    val effect: SharedFlow<HistoryUiEffect> = _effect.asSharedFlow()

    fun loadHistory() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = getHistoryUseCase()) {
                is Result.Success -> {
                    val films = buildFilmsFromHistory(result.data)
                    _state.value = _state.value.copy(films = films, isLoading = false)
                }

                is Result.Error -> _state.value = _state.value.copy(isLoading = false, errorMessage = result.errorMessage)
                is Result.Loading -> {}
            }
        }
    }

    private suspend fun buildFilmsFromHistory(historyItems: List<HistoryItem>): List<HistoryFilm> {
        val slugs = historyItems.map { it.slug }
        return when (val result = getFilmDetailsUseCase.batch(slugs)) {
            is Result.Success -> {
                val detailsMap = result.data
                historyItems.mapNotNull { item ->
                    detailsMap[item.slug]?.let { fd ->
                        HistoryFilm(
                            slug = fd.slug,
                            title = fd.title,
                            posterUrl = fd.posterUrl,
                            description = fd.description,
                            watchedAt = item.watchedAt,
                        )
                    }
                }
            }

            is Result.Error -> emptyList()
            is Result.Loading -> emptyList()
        }
    }

    fun deleteHistory(slug: String) {
        viewModelScope.launch {
            try {
                deleteHistoryUseCase(slug)
                val updated = _state.value.films.filter { it.slug != slug }
                _state.value = _state.value.copy(films = updated)
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "deleteHistory failed", e)
            }
        }
    }

    fun addToHistory(slug: String, episode: Int, position: Long) {
        viewModelScope.launch {
            addToHistoryUseCase(slug, episode, position)
        }
    }

    suspend fun getHistoryItem(slug: String): HistoryItem? {
        return when (val result = getHistoryUseCase()) {
            is Result.Success -> result.data.find { it.slug == slug }
            is Result.Error -> {
                Log.e("HistoryViewModel", "getHistoryItem failed", result.exception)
                null
            }

            is Result.Loading -> null
        }
    }

    fun onFilmClicked(slug: String) {
        viewModelScope.launch {
            _effect.emit(HistoryUiEffect.NavigateToFilm(slug))
        }
    }
}

