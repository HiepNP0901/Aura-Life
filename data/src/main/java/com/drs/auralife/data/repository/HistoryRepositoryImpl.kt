package com.drs.auralife.data.repository

import com.drs.auralife.domain.result.Result
import com.drs.auralife.core.database.dao.HistoryDao
import com.drs.auralife.core.database.mapper.LocalMapper.toDomainHistoryItem
import com.drs.auralife.core.database.mapper.LocalMapper.toHistoryEntity
import com.drs.auralife.core.firebase.FirebaseMapper.toDomainHistoryItems
import com.drs.auralife.core.firebase.HistoryDataSource
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.repository.HistoryRepository
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao,
    private val historyDataSource: HistoryDataSource,
) : HistoryRepository {
    override suspend fun getHistory(): Result<List<HistoryItem>> {
        return try {
            val history = historyDataSource.getHistoryData().toDomainHistoryItems()
            historyDao.clear()
            history.forEach { historyDao.insertHistory(it.toHistoryEntity()) }
            Result.Success(history)
        } catch (e: Exception) {
            val cached = historyDao.getAll().map { it.toDomainHistoryItem() }
            if (cached.isNotEmpty()) Result.Success(cached) else Result.Error(e)
        }
    }

    override suspend fun deleteHistory(slug: String): Boolean {
        return historyDataSource.deleteHistory(slug)
    }

    override suspend fun addHistory(slug: String, episode: Int, position: Long): Boolean {
        return historyDataSource.addHistoryData(slug, episode, position)
    }
}
