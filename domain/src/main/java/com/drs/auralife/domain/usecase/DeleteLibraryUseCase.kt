package com.drs.auralife.domain.usecase

import com.drs.auralife.domain.repository.LibraryRepository
import javax.inject.Inject

class DeleteLibraryUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository,
) {
    suspend operator fun invoke(name: String): Boolean {
        return libraryRepository.deleteLibrary(name)
    }
}
