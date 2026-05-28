package com.drs.auralife.data.repository

import com.drs.auralife.data.firebase.realtime.database.category.CategoryRepository as FirebaseCategoryRepository
import com.drs.auralife.data.mapper.FirebaseMapper.toDomainCategories
import com.drs.auralife.domain.model.Category
import com.drs.auralife.domain.repository.CategoryRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CategoryRepositoryImpl : CategoryRepository {
    override suspend fun getCategories(): List<Category> {
        return suspendCancellableCoroutine { continuation ->
            FirebaseCategoryRepository.getCategoryData { firebaseCategories ->
                continuation.resume(firebaseCategories.toDomainCategories())
            }
        }
    }
}
