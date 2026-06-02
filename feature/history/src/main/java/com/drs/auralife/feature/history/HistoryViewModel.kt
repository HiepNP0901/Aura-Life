package com.drs.auralife.feature.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.usecase.AddToHistoryUseCase
import com.drs.auralife.domain.usecase.DeleteHistoryUseCase
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
import com.drs.auralife.domain.usecase.GetHistoryUseCase
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
            try {
                val historyItems = getHistoryUseCase()
                val films = buildFilmsFromHistory(historyItems)
                _state.value = _state.value.copy(films = films, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    private suspend fun buildFilmsFromHistory(historyItems: List<HistoryItem>): List<Film> {
        val slugs = historyItems.map { it.slug }
        val detailsMap = getFilmDetailsUseCase.batch(slugs)
        return historyItems.mapNotNull { item ->
            detailsMap[item.slug]?.let { fd ->
                Film(
                    id = fd.slug,
                    slug = fd.slug,
                    title = fd.title,
                    posterUrl = fd.posterUrl,
                    thumbUrl = fd.thumbUrl,
                    description = fd.description,
                    category = fd.categories?.firstOrNull() ?: "",
                    episodeCount = fd.episodeTotal?.toIntOrNull() ?: 0,
                    watchedAt = item.watchedAt,
                )
            }
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
        return try {
            getHistoryUseCase().find { it.slug == slug }
        } catch (e: Exception) {
            Log.e("HistoryViewModel", "getHistoryItem failed", e)
            null
        }
    }

    fun onFilmClicked(slug: String) {
        viewModelScope.launch {
            _effect.emit(HistoryUiEffect.NavigateToFilm(slug))
        }
    }
}
