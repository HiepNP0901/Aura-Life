package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.repository.CategoryRepository

class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(): List<Category> {
        return categoryRepository.getCategories()
    }
}
