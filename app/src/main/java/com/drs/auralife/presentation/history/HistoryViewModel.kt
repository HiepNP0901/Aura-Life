package com.drs.auralife.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.usecase.AddToHistoryUseCase
import com.drs.auralife.domain.usecase.DeleteHistoryUseCase
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
import com.drs.auralife.domain.usecase.GetHistoryUseCase
import com.drs.auralife.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _filmsState = MutableStateFlow<UiState<List<Film>>>(UiState.Loading)
    val filmsState: StateFlow<UiState<List<Film>>> = _filmsState.asStateFlow()

    fun loadHistory() {
        viewModelScope.launch {
            _filmsState.value = UiState.Loading
            _filmsState.value = try {
                val historyItems = getHistoryUseCase()
                val films = buildFilmsFromHistory(historyItems)
                UiState.Success(films)
            } catch (e: Exception) {
                UiState.Error(e.message ?: "Failed to load history")
            }
        }
    }

    private suspend fun buildFilmsFromHistory(historyItems: List<HistoryItem>): List<Film> {
        return coroutineScope {
            historyItems.map { item ->
                async {
                    val details = getFilmDetailsUseCase(item.slug)
                    details?.let { fd ->
                        Film(
                            id = fd.slug,
                            slug = fd.slug,
                            title = fd.title,
                            posterUrl = fd.posterUrl,
                            thumbUrl = fd.thumbUrl,
                            description = fd.description,
                            category = fd.categories?.firstOrNull() ?: "",
                            episodeCount = fd.episodeTotal?.toIntOrNull() ?: 0,
                        )
                    }
                }
            }.mapNotNull { it.await() }
                .sortedByDescending { historyItems.indexOfFirst { h -> h.slug == it.slug } }
        }
    }

    fun deleteHistory(slug: String) {
        viewModelScope.launch {
            try {
                deleteHistoryUseCase(slug)
                val current = _filmsState.value
                if (current is UiState.Success) {
                    _filmsState.value = UiState.Success(current.data.filter { it.slug != slug })
                }
            } catch (_: Exception) {
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
        } catch (_: Exception) {
            null
        }
    }
}
