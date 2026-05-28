package com.drs.auralife.domain.repository

import com.drs.auralife.domain.model.Category

interface CategoryRepository {
    suspend fun getCategories(): List<Category>
}
