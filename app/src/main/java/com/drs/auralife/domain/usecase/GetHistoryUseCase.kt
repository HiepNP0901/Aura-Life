package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.repository.HistoryRepository

class GetHistoryUseCase(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(): List<HistoryItem> {
        return historyRepository.getHistory()
    }
}
