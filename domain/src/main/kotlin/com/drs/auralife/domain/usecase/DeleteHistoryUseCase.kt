package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.HistoryRepository

class DeleteHistoryUseCase @javax.inject.Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(slug: String): Boolean {
        return historyRepository.deleteHistory(slug)
    }
}
