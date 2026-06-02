package com.drs.auralife.data.repository

import com.drs.auralife.core.database.dao.CategoryCacheDao
import com.drs.auralife.core.database.mapper.LocalMapper.toCategoryCacheEntity
import com.drs.auralife.core.database.mapper.LocalMapper.toDomainCategory
import com.drs.auralife.core.network.FilmAPI
import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.repository.CategoryRepository
import com.drs.auralife.domain.result.Result
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val filmAPI: FilmAPI,
    private val categoryCacheDao: CategoryCacheDao,
) : CategoryRepository {
    override suspend fun getCategories(): Result<List<Category>> {
        return try {
            val categories = filmAPI.getCategories().map { apiCategory ->
                Category(
                    slug = apiCategory.slug,
                    name = apiCategory.name,
                    localizedName = apiCategory.name,
                )
            }
            categoryCacheDao.clear()
            categoryCacheDao.insertAll(categories.map { it.toCategoryCacheEntity() })
            Result.Success(categories)
        } catch (e: Exception) {
            val cached = categoryCacheDao.getAll().map { it.toDomainCategory() }
            if (cached.isNotEmpty()) Result.Success(cached) else Result.Error(e)
        }
    }
}
