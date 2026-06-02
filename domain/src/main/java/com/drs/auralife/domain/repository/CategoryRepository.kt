package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.result.Result

interface CategoryRepository {
    suspend fun getCategories(): Result<List<Category>>
}
