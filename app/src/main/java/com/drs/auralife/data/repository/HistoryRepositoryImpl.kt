package com.drs.auralife.data.repository

import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.repository.HistoryRepository

class HistoryRepositoryImpl : HistoryRepository {
    override suspend fun getHistory(): List<HistoryItem> {
        TODO("Implement Firebase history retrieval and mapping")
    }

    override suspend fun deleteHistory(slug: String): Boolean {
        TODO("Implement Firebase history deletion")
    }
}
