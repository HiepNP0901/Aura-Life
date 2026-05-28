package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.HistoryItem

interface HistoryRepository {
    suspend fun getHistory(): List<HistoryItem>
    suspend fun addHistory(slug: String, episode: Int, position: Long): Boolean
    suspend fun deleteHistory(slug: String): Boolean
}
