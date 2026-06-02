package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.result.Result
import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.repository.CategoryRepository

class GetCategoriesUseCase @javax.inject.Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(): Result<List<Category>> {
        return categoryRepository.getCategories()
    }
}
