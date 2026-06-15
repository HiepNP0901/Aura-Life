package com.drs.auralife.data.repository

import com.drs.auralife.core.database.dao.HistoryDao
import com.drs.auralife.core.database.mapper.LocalMapper.toDomainHistoryItem
import com.drs.auralife.core.database.mapper.LocalMapper.toHistoryEntity
import com.drs.auralife.core.firebase.FirebaseMapper.toDomainHistoryItems
import com.drs.auralife.core.firebase.HistoryDataSource
import com.drs.auralife.domain.model.HistoryItem
import com.drs.auralife.domain.repository.HistoryRepository
import com.drs.auralife.domain.result.Result
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao,
    private val historyDataSource: HistoryDataSource,
) : HistoryRepository {

    override suspend fun getHistory(): Result<List<HistoryItem>> {
        return try {
            val firebaseHistory = historyDataSource.getHistoryData().toDomainHistoryItems()
            for (item in firebaseHistory) {
                val existing = historyDao.getHistoryItem(item.slug)
                if (existing == null || item.watchedAt > existing.watchedAt) {
                    historyDao.insertHistory(item.toHistoryEntity())
                }
            }
            Result.Success(historyDao.getAll().map { it.toDomainHistoryItem() })
        } catch (e: Exception) {
            val cached = historyDao.getAll().map { it.toDomainHistoryItem() }
            if (cached.isNotEmpty()) Result.Success(cached) else Result.Error(e)
        }
    }

    override suspend fun getHistoryItem(slug: String): HistoryItem? {
        return historyDao.getHistoryItem(slug)?.toDomainHistoryItem()
    }

    override suspend fun addHistory(slug: String, episode: Int, position: Long): Boolean {
        val entity = HistoryItem(
            slug = slug,
            title = "",
            watchedAt = System.currentTimeMillis(),
            episode = episode,
            position = position,
        ).toHistoryEntity()
        historyDao.insertHistory(entity)
        val firebaseSuccess = historyDataSource.addHistoryData(slug, episode, position)
        if (!firebaseSuccess) Log.w("HistoryRepo", "addHistory: Firebase sync failed")
        return firebaseSuccess
    }

    override suspend fun deleteHistory(slug: String): Boolean {
        historyDao.deleteHistory(slug)
        val firebaseSuccess = historyDataSource.deleteHistory(slug)
        if (!firebaseSuccess) Log.w("HistoryRepo", "deleteHistory: Firebase sync failed")
        return firebaseSuccess
    }
}
