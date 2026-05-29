package com.drs.auralife.data.repository

import com.drs.auralife.data.local.dao.HistoryDao
import com.drs.auralife.data.local.mapper.LocalMapper.toDomainHistoryItem
import com.drs.auralife.data.local.mapper.LocalMapper.toHistoryEntity
import com.drs.auralife.data.remote.firebase.HistoryDataSource
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.repository.HistoryRepository
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao,
) : HistoryRepository {
    override suspend fun getHistory(): List<HistoryItem> {
        return try {
            val history = HistoryDataSource.getHistoryData().map { it.toDomainHistoryItem() }
            historyDao.clear()
            history.forEach { historyDao.insertHistory(it.toHistoryEntity()) }
            history
        } catch (e: Exception) {
            historyDao.getAll().map { it.toDomainHistoryItem() }
        }
    }

    override suspend fun deleteHistory(slug: String): Boolean {
        return HistoryDataSource.deleteHistory(slug)
    }

    override suspend fun addHistory(slug: String, episode: Int, position: Long): Boolean {
        return HistoryDataSource.addHistoryData(slug, episode, position)
    }
}
