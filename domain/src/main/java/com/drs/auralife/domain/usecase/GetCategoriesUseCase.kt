package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.repository.CategoryRepository
import com.drs.auralife.domain.result.Result

class GetCategoriesUseCase @javax.inject.Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(): Result<List<Category>> {
        return categoryRepository.getCategories()
    }
}
