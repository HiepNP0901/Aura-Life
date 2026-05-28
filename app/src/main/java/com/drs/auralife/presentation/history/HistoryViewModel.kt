package com.drs.auralife.presentation.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drs.auralife.domain.model.Film
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.repository.AuthRepository
import com.drs.auralife.domain.usecase.DeleteHistoryUseCase
import com.drs.auralife.domain.usecase.GetFilmDetailsUseCase
import com.drs.auralife.domain.usecase.GetHistoryUseCase
import com.drs.auralife.domain.repository.HistoryRepository
import com.drs.auralife.core.utils.HistoryUtils
import com.drs.auralife.core.utils.Time
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val historyRepository: HistoryRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _filmsState = MutableStateFlow<List<Film>>(emptyList())
    val filmsState: StateFlow<List<Film>> = _filmsState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadHistory(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val historyItems = if (authRepository.isLoggedIn()) {
                    getHistoryUseCase()
                } else {
                    HistoryUtils.getLocalHistories(context).map {
                        HistoryItem(
                            slug = it.slug,
                            title = "",
                            watchedAt = it.date.toLongOrNull() ?: System.currentTimeMillis(),
                        )
                    }
                }
                buildFilmsFromHistory(historyItems, context)
            } catch (_: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun buildFilmsFromHistory(
        historyItems: List<HistoryItem>,
        context: Context,
    ) {
        val tempList = mutableListOf<Film>()
        for (item in historyItems) {
            val details = getFilmDetailsUseCase(item.slug)
            details?.let { fd ->
                val timeDiff = Time.calculateTimeDifference(
                    java.time.Instant.ofEpochMilli(item.watchedAt),
                    context,
                )
                tempList.add(
                    Film(
                        id = fd.slug,
                        slug = fd.slug,
                        title = fd.title,
                        posterUrl = fd.posterUrl,
                        thumbUrl = fd.thumbUrl,
                        description = "$timeDiff<br>${fd.description}",
                        category = fd.categories?.firstOrNull() ?: "",
                        episodeCount = fd.episodeTotal?.toIntOrNull() ?: 0,
                    )
                )
            }
        }
        _filmsState.value = historyItems
            .mapNotNull { h -> tempList.find { it.slug == h.slug } }
            .reversed()
    }

    fun deleteHistory(
        context: Context,
        slug: String,
    ) {
        viewModelScope.launch {
            if (authRepository.isLoggedIn()) {
                deleteHistoryUseCase(slug)
            } else {
                HistoryUtils.removeLocalHistory(context, slug)
            }
            _filmsState.value = _filmsState.value.filter { it.slug != slug }
        }
    }

    fun addToHistory(
        slug: String,
        episode: Int,
        position: Long,
    ) {
        viewModelScope.launch {
            historyRepository.addHistory(slug, episode, position)
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
