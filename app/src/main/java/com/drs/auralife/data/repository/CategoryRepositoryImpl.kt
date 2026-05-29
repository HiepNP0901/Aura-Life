package com.drs.auralife.data.repository

import com.drs.auralife.data.firebase.realtime.database.category.CategoryRepository as FirebaseCategoryRepository
import com.drs.auralife.data.local.dao.CategoryCacheDao
import com.drs.auralife.data.local.mapper.LocalMapper.toCategoryCacheEntity
import com.drs.auralife.data.local.mapper.LocalMapper.toDomainCategory
import com.drs.auralife.data.mapper.FirebaseMapper.toDomainCategories
import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.repository.CategoryRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryCacheDao: CategoryCacheDao,
) : CategoryRepository {
    override suspend fun getCategories(): List<Category> {
        return try {
            val categories = suspendCancellableCoroutine<List<Category>> { continuation ->
                FirebaseCategoryRepository.getCategoryData { firebaseCategories ->
                    continuation.resume(firebaseCategories.toDomainCategories())
                }
            }
            categoryCacheDao.clear()
            categoryCacheDao.insertAll(categories.map { it.toCategoryCacheEntity() })
            categories
        } catch (e: Exception) {
            categoryCacheDao.getAll().map { it.toDomainCategory() }
        }
    }
}
