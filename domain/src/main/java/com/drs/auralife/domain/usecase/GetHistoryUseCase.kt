package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.repository.HistoryRepository

class GetHistoryUseCase @javax.inject.Inject constructor(
    private val historyRepository: HistoryRepository,
) {
    suspend operator fun invoke(): Result<List<HistoryItem>> {
        return historyRepository.getHistory()
    }
}
