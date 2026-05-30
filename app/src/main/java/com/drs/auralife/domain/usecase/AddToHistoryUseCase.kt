package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.HistoryRepository

class AddToHistoryUseCase @javax.inject.Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(slug: String, episode: Int, position: Long): Boolean {
        return historyRepository.addHistory(slug, episode, position)
    }
}
