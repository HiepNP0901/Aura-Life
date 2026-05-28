package com.drs.auralife.data.repository

import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.repository.CategoryRepository

class CategoryRepositoryImpl : CategoryRepository {
    override suspend fun getCategories(): List<Category> {
        TODO("Map Firebase category data to domain model")
    }
}
